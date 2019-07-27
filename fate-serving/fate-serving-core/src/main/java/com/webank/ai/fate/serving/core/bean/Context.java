package com.webank.ai.fate.serving.core.bean;

public interface Context {
//    public Object  getData(Object  key);
//    public void   putData(Object  key,Object data);
    public String getCaseId();
    public void setCaseId(String caseId);

}
