package com.webank.ai.fate.serving.core.bean;

public interface Context {
    Object  getData(Object  key);
    void   putData(Object  key,Object data);
}
