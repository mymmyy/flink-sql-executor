package com.mym.flink.sqlexecutor.sqlclient.bean;

import com.mym.flink.sqlexecutor.sqlclient.SqlClient;

import java.util.Map;

/**
 * 抽象任务切面描述器（增强、扩展任务构建过程）
 * <br/>一个SQL任务大致几个过程：本地SqlClient初始化、解析参数和解析SQL、构建流（批）执行环境（环境配置、注册UDF等）、构建执行图、提交执行
 *
 * @author maoym
 */
public abstract class AbstractTaskAspectsDescriptor {

    /**
     * 配置程序参数，设置的程序参数可在sql文件中和自定算子中访问
     *
     * @param args      args参数
     * @param sqlClient sql客户端
     * @return {@link Map}<{@link String}, {@link String}>
     */
    public abstract Map<String, String> configProgramParam(String[] args, SqlClient sqlClient);

    /**
     * 配置Flink运行参数，自动设置到Stream的env中。return null 将使用默认值
     *
     * @param args      args参数
     * @param sqlClient sql客户端
     * @return {@link JobEnvConfig}
     */
    public abstract JobEnvConfig configJobEnvSetting(String[] args, SqlClient sqlClient);

    /**
     * 解析外部参数前：SqlClient初始化后，解析sql文件前
     *
     * @param args      main参数
     * @param sqlClient sqlClient
     */
    public abstract void beforeParseParam(String[] args, SqlClient sqlClient);

    /**
     * 任务执行前：一切准备就绪（SQL已解析完毕、Graph执行流图已经构建完毕），等待execute
     *
     * @param args      main参数
     * @param sqlClient sqlClient
     */
    public abstract void beforeExecute(String[] args, SqlClient sqlClient);

    /**
     * 任务执行后（流计算任务只是提交完任务）
     *
     * @param args      main参数
     * @param sqlClient sqlClient
     */
    public abstract void afterExecute(String[] args, SqlClient sqlClient);

    /**
     * 注册udf，调用sqlClient的registerUdf方法
     *
     * @param sqlClient sqlClient
     */
    public abstract void registerUdf(SqlClient sqlClient);

    /**
     * 注册自定义算子（一般写在sql文件中），调用sqlClient的registerCustomOperator方法
     *
     * @param sqlClient sqlClient
     */
    public abstract void registerCustomOperator(SqlClient sqlClient);
}
