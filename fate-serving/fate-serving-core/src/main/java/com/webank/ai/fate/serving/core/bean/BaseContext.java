package com.webank.ai.fate.serving.core.bean;

import com.google.common.collect.Maps;

import java.util.Map;


public class BaseContext implements Context {

    final String CASEID= "caseId";

    Map data = Maps.newHashMap();
//    @Override
//    public Object getData(Object key) {
//        return  data.get(key);
//    }
//    @Override
//    public  void putData(Object  key,Object value){
//        data.put(key,value);
//    }
    @Override
    public String getCaseId() {
        if(data.get(CASEID)!=null){
            return  data.get(CASEID).toString();
        }
        else {
            return null;
        }
    }

    @Override
    public void setCaseId(String caseId){
        data.put(CASEID,caseId);
    }
}
