package com.mym.flink.sqlexecutor.sqlclient.bean;

import com.mym.flink.sqlexecutor.sqlclient.SqlClient;

public class DefaultEmptyTaskAspectsDescriptor extends AbstractTaskAspectsDescriptor{
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
