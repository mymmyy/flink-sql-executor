package com.mym.flink.sqlexecutor.sqlclient.example;

import com.mym.flink.sqlexecutor.sqlclient.SqlClient;

public class ExampleSqlExecutor {

    public static void main(String[] args) throws Exception {
        ExampleTaskAspectsDescriptor exampleTaskAspectsDescriptor = new ExampleTaskAspectsDescriptor();
        new SqlClient(exampleTaskAspectsDescriptor).execute(args, "sql");
    }
}
