from concurrent import futures

import grpc
import sys
import time
from flask import Flask
from grpc._cython import cygrpc
from werkzeug.serving import run_simple
from werkzeug.wsgi import DispatcherMiddleware

from arch.api.proto import proxy_pb2_grpc
from fate_flow.apps.data_access_app import manager as data_access_app_manager
from fate_flow.apps.job_apps import manager as job_app_manager
from fate_flow.apps.machine_learning_model_app import manager as model_app_manager
from fate_flow.apps.pipeline_app import manager as pipeline_app_manager
from fate_flow.apps.table_app import manager as table_app_manager
from fate_flow.apps.tracking_app import manager as tracking_app_manager
from fate_flow.db.db_models import init_tables
from fate_flow.driver import dag_scheduler, job_controller, job_detector
from fate_flow.entity.runtime_config import RuntimeConfig
from fate_flow.manager import queue_manager
from fate_flow.settings import IP, GRPC_PORT, HTTP_PORT, _ONE_DAY_IN_SECONDS, MAX_CONCURRENT_JOB_RUN, stat_logger, \
    API_VERSION, WORK_MODE
from fate_flow.storage.fate_storage import FateStorage
from fate_flow.utils.api_utils import get_json_result
from fate_flow.utils.grpc_utils import UnaryServicer

'''
Initialize the manager
'''

manager = Flask(__name__)


@manager.errorhandler(500)
def internal_server_error(e):
    stat_logger.exception(e)
    return get_json_result(retcode=100, retmsg=str(e))


if __name__ == '__main__':
    manager.url_map.strict_slashes = False
    app = DispatcherMiddleware(
        manager,
        {
            '/{}/data'.format(API_VERSION): data_access_app_manager,
            '/{}/model'.format(API_VERSION): model_app_manager,
            '/{}/job'.format(API_VERSION): job_app_manager,
            '/{}/table'.format(API_VERSION): table_app_manager,
            '/{}/tracking'.format(API_VERSION): tracking_app_manager,
            '/{}/pipeline'.format(API_VERSION): pipeline_app_manager,
        }
    )
    # init
    RuntimeConfig.init_config({'WORK_MODE': WORK_MODE})
    FateStorage.init_storage()
    init_tables()
    queue_manager.init_job_queue()
    job_controller.JobController.init()
    # start job detector
    job_detector.JobDetector(interval=5 * 1000).start()
    # start scheduler
    scheduler = dag_scheduler.DAGScheduler(queue=RuntimeConfig.JOB_QUEUE, concurrent_num=MAX_CONCURRENT_JOB_RUN)
    scheduler.start()
    # start grpc server
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10),
                         options=[(cygrpc.ChannelArgKey.max_send_message_length, -1),
                                  (cygrpc.ChannelArgKey.max_receive_message_length, -1)])

    proxy_pb2_grpc.add_DataTransferServiceServicer_to_server(UnaryServicer(), server)
    server.add_insecure_port("{}:{}".format(IP, GRPC_PORT))
    server.start()
    # start http server
    run_simple(hostname=IP, port=HTTP_PORT, application=app, threaded=True)

    try:
        while True:
            time.sleep(_ONE_DAY_IN_SECONDS)
    except KeyboardInterrupt:
        server.stop(0)
        sys.exit(0)
