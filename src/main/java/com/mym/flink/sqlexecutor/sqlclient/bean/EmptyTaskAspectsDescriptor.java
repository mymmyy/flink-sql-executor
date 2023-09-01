package com.mym.flink.sqlexecutor.sqlclient.bean;

import com.mym.flink.sqlexecutor.sqlclient.SqlClient;

import java.util.Map;

public class EmptyTaskAspectsDescriptor extends AbstractTaskAspectsDescriptor{
    @Override
    public Map<String, String> configProgramParam(String[] args, SqlClient sqlClient) {
        return null;
    }

    @Override
    public JobEnvConfig configJobEnvSetting(String[] args, SqlClient sqlClient) {
        return null;
    }

    @Override
    public void beforeParseParam(String[] args, SqlClient sqlClient) {

    }

    @Override
    public void beforeExecute(String[] args, SqlClient sqlClient) {

    }

    @Override
    public void afterExecute(String[] args, SqlClient sqlClient) {

    }

    @Override
    public void registerUdf(SqlClient sqlClient) {

    }

    @Override
    public void registerCustomOperator(SqlClient sqlClient) {

    }
}
