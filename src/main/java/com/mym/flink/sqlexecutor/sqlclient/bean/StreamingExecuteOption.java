package com.mym.flink.sqlexecutor.sqlclient.bean;

import com.mym.flink.sqlexecutor.sqlclient.enumtype.ExecutionOptions;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.time.Duration;

public class StreamingExecuteOption implements FlinkExecuteOption {

    private final ExecutionOptions executionMode = ExecutionOptions.STREAMING;

    private StreamExecutionEnvironment streamEnvironment;

    private StreamTableEnvironment tableEnvironment;

    private EnvironmentSettings environmentSettings;

    private String jobName = "";

    private JobEnvConfig jobEnvConfig;

    private ParameterTool parameterTool;

    public StreamingExecuteOption(ParameterTool parameterTool) {
        this(parameterTool, "sql-job", new JobEnvConfig());
    }

    public StreamingExecuteOption(ParameterTool parameterTool, String jobName, JobEnvConfig jobEnvConfig) {
        this.parameterTool = parameterTool;
        this.jobName = jobName;
        this.streamEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        this.environmentSettings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        this.tableEnvironment = StreamTableEnvironment.create(streamEnvironment, environmentSettings);
        this.jobEnvConfig = jobEnvConfig;

        initConfig();
    }

    private void initConfig(){
        if(this.jobEnvConfig.getParallelism() != null) {
            this.streamEnvironment.setParallelism(this.jobEnvConfig.getParallelism());
        }
        this.tableEnvironment.getConfig().setIdleStateRetention(Duration.ofSeconds(this.jobEnvConfig.getIdleStateRetentionMin()));

        /* parse args */

    }

    @Override
    public ExecutionOptions getExecutionOptions() {
        return this.executionMode;
    }

    public void execute() throws Exception {
        this.streamEnvironment.execute(this.jobName);
    }

    public ExecutionOptions getExecutionMode() {
        return this.executionMode;
    }

    public StreamExecutionEnvironment getStreamEnvironment() {
        return this.streamEnvironment;
    }

    public StreamTableEnvironment getTableEnvironment() {
        return this.tableEnvironment;
    }

    public EnvironmentSettings getEnvironmentSettings() {
        return this.environmentSettings;
    }

    public String getJobName() {
        return this.jobName;
    }

    public JobEnvConfig getJobEnvConfig() {
        return this.jobEnvConfig;
    }
}
