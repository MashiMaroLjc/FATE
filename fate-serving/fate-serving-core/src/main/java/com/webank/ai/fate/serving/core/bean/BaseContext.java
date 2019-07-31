package com.webank.ai.fate.serving.core.bean;

import com.google.common.collect.Maps;
import com.webank.ai.fate.core.bean.ReturnResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;


public class BaseContext<Req ,Resp extends ReturnResult> implements Context<Req,Resp> {


    private static final Logger LOGGER = LogManager.getLogger(LOGGER_NAME);

    String actionType;

    final long  timestamp = System.currentTimeMillis();

    Map dataMap = Maps.newHashMap();

    @Override
    public void preProcess() {


    }

    @Override
    public Object getData(Object key) {
        return null;
    }




    @Override
    public void putData(Object key, Object data) {

        dataMap.put(key,data);

    }

    @Override
    public String getCaseId() {
        if(dataMap.get(Dict.CASEID)!=null){
            return  dataMap.get(Dict.CASEID).toString();
        }
        else {
            return null;
        }
    }

    @Override
    public void setCaseId(String caseId){
        dataMap.put(Dict.CASEID,caseId);
    }

    @Override
    public long getTimeStamp() {
        return timestamp;
    }

    @Override
    public void postProcess(Req  req,Resp  resp) {
        try {
            long now = System.currentTimeMillis();
            String reqData = this.dataMap.get(Dict.ORIGIN_REQUEST) != null ? this.dataMap.get(Dict.ORIGIN_REQUEST).toString() : "";
            reqData = "";
            if (req instanceof Request) {
                LOGGER.info("caseid {} type {} costtime {} return_code {} req {} ", req != null ? ((Request) req).getCaseid() : "NONE", actionType, now - timestamp,
                        resp != null ? resp.getRetcode() : "NONE", reqData
                );
            }
            if (req instanceof Map) {
                LOGGER.info("caseid {} type {} costtime {} return_code {} req {}",
                        req != null ? ((Map) req).get(Dict.CASEID) : "NONE", actionType, now - timestamp,
                        resp != null ? resp.getRetcode() : "NONE", reqData
                );
            }
        }catch(Throwable  e){


        }
    }

    @Override
    public void setActionType(String actionType) {
        this.actionType=  actionType;

    }
}
