package com.webank.ai.fate.serving.core.bean;

import com.google.common.collect.Maps;

import java.util.Map;


public class BaseContext implements Context {

    Map data = Maps.newHashMap();
    @Override
    public Object getData(Object key) {
        return  data.get(key);
    }
    @Override
    public  void putData(Object  key,Object value){
        data.put(key,value);

    }
}
