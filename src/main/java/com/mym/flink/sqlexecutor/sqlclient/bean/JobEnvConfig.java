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
        this.idleStateRetentionMax = this.idleStateRetentionMin + 300;
    }

    public Long getIdleStateRetentionMax() {
        return idleStateRetentionMax;
    }

    public Integer getParallelism() {
        return parallelism;
    }

    public void setParallelism(Integer parallelism) {
        this.parallelism = parallelism;
    }

    public void merge(JobEnvConfig mergeConfig){
        if(mergeConfig == null){
            return;
        }
        this.setParallelism(mergeConfig.getParallelism() == null ? this.getParallelism() : mergeConfig.getParallelism());
        this.setIdleStateRetentionMin(mergeConfig.getIdleStateRetentionMin() == null ? this.getIdleStateRetentionMin() : mergeConfig.getIdleStateRetentionMin());
    }
}
