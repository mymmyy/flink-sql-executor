package com.mym.flink.sqlexecutor.sqlclient.bean;


import com.mym.flink.sqlexecutor.sqlclient.SqlClient;
import com.mym.flink.sqlexecutor.sqlclient.enumtype.ColumnType;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 默认 任务描述器（方便系统默认预设配置）
 *
 * @author maoym
 * @date 2023/09/01
 */
public class DefaultTaskAspectsDescriptor extends AbstractTaskAspectsDescriptor{
    @Override
    public Map<String, String> configProgramParam(String[] args, SqlClient sqlClient) {
        HashMap<String, String> commonParam = new HashMap<>();

        return commonParam;
    }

    @Override
    public JobEnvConfig configJobEnvSetting(String[] args, SqlClient sqlClient) {
        JobEnvConfig jobEnvConfig = new JobEnvConfig();
        jobEnvConfig.setIdleStateRetentionMin(60L);
        return jobEnvConfig;
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
        SqlUdfDescriptor jsonValueUDFDescriptor = new SqlUdfDescriptor();
        jsonValueUDFDescriptor.setDesc("从输入的 JSON 字符串中提取标量值.");
        jsonValueUDFDescriptor.setCallName("JSON_VALUE");
        jsonValueUDFDescriptor.setReturnType(ColumnType.STRING);
        jsonValueUDFDescriptor.setClassFullName("com.mym.flink.sqlexecutor.sqlclient.udf.JsonValueUDF");
        LinkedHashMap<String, ColumnType> argsMap = new LinkedHashMap<String, ColumnType>();
        argsMap.put("jsonString", ColumnType.STRING);
        argsMap.put("keyPath", ColumnType.STRING);
        jsonValueUDFDescriptor.setArgsMap(argsMap);
        sqlClient.registerUdf(jsonValueUDFDescriptor);

        // 其他UDF注册
    }

    @Override
    public void registerCustomOperator(SqlClient sqlClient) {

    }

}
