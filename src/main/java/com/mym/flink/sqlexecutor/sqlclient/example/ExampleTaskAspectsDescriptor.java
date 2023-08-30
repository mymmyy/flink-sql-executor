package com.mym.flink.sqlexecutor.sqlclient.example;

import com.mym.flink.sqlexecutor.sqlclient.SqlClient;
import com.mym.flink.sqlexecutor.sqlclient.bean.AbstractTaskAspectsDescriptor;

import java.util.Map;

/**
 * 示例
 *
 * @author maoym
 */
public class ExampleTaskAspectsDescriptor extends AbstractTaskAspectsDescriptor {
    @Override
    public Map<String, String> configProgramParam(String[] args, SqlClient sqlClient) {
        return null;
    }

    @Override
    public void beforeParseParam(String[] args, SqlClient sqlClient) {
        System.out.println("example Task Aspects, beforeParseParam!");
    }

    @Override
    public void beforeExecute(String[] args, SqlClient sqlClient) {
        System.out.println("example Task Aspects, beforeExecute!");
    }

    @Override
    public void afterExecute(String[] args, SqlClient sqlClient) {
        System.out.println("example Task Aspects, afterExecute!");
    }

    @Override
    public void registerUdf(SqlClient sqlClient) {
        System.out.println("example Task Aspects, registerUdf!");
    }

    @Override
    public void registerCustomOperator(SqlClient sqlClient) {
        System.out.println("example Task Aspects, registerCustomOperator!");
    }
}
