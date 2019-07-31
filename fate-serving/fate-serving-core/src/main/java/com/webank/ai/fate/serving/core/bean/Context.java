package com.webank.ai.fate.serving.core.bean;

public interface Context <Req,Resp>{


    static final   String  LOGGER_NAME =  "flow";
    public void  preProcess();
    public Object  getData(Object  key);
    public void   putData(Object  key,Object data);
    public String getCaseId();
    public void setCaseId(String caseId);
    public long  getTimeStamp();
    public default void  postProcess(Req req,Resp resp){};
    public void  setActionType(String actionType);



}
