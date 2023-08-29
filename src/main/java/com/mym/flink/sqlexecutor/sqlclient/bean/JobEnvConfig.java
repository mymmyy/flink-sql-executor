package com.mym.flink.sqlexecutor.sqlclient.bean;

/**
 * 工作env配置
 *
 * @author maoym
 * @date 2023/08/25
 */
public class JobEnvConfig {


    /**
     * 并行度
     */
    private Integer parallelism;

    /**
     * 空闲状态保留最小时间 秒
     */
    private Long idleStateRetentionMin = 60L;

    /**
     * 空闲状态保留最大时间 秒  建议 idleStateRetentionMax = idleStateRetentionMin + 5minute
     */
    private Long idleStateRetentionMax = 360L;

    public JobEnvConfig() {
    }

    public Long getIdleStateRetentionMin() {
        return idleStateRetentionMin;
    }

    public void setIdleStateRetentionMin(Long idleStateRetentionMin) {
        this.idleStateRetentionMin = idleStateRetentionMin;
    }

    public Long getIdleStateRetentionMax() {
        return idleStateRetentionMax;
    }

    public void setIdleStateRetentionMax(Long idleStateRetentionMax) {
        this.idleStateRetentionMax = idleStateRetentionMax;
    }

    public Integer getParallelism() {
        return parallelism;
    }

    public void setParallelism(Integer parallelism) {
        this.parallelism = parallelism;
    }
}