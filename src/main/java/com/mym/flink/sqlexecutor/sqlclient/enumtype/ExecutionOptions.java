package com.mym.flink.sqlexecutor.sqlclient.enumtype;

public enum ExecutionOptions {

    STREAMING(1),
    BATCH(2),

    ;

    int code;

    ExecutionOptions(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
