package com.mym.flink.sqlexecutor.sqlclient.bean;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCustomOperator implements CustomOperator{

    private Map<String, String> paramMap = new HashMap<>();

    @Override
    public Map<String, String> getParamMap() {
        return this.paramMap;
    }
}
