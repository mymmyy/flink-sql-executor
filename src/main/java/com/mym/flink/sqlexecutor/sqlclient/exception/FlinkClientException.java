package com.mym.flink.sqlexecutor.sqlclient.exception;

public class FlinkClientException extends RuntimeException {

    public FlinkClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlinkClientException(String message) {
        super(message);
    }
}
