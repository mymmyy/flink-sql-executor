package com.mym.flink.sqlexecutor.sqlclient.graph;

import com.mym.flink.sqlexecutor.sqlclient.enumtype.GraphNodeOptions;

import java.util.List;

public interface GraphNode {

    /**
     * 获取GraphNodeOptions 节点类型
     *
     * @return {@link GraphNodeOptions}
     */
    GraphNodeOptions getGraphNodeOptions();

    /**
     * 获取 节点名
     *
     * @return {@link String}
     */
    String getNodeName();

    /**
     * 获取 临时表名（当前结果作为一个临时表，下游使用）
     *
     * @return {@link String}
     */
    String getTemporaryTableName();

    /**
     * 获取 节点描述
     *
     * @return {@link String}
     */
    String getDesc();

    /**
     * 获取上游Node列表
     *
     * @return {@link GraphNode}
     */
    List<GraphNode> getLastNode();

    /**
     * 获取下游Node列表
     *
     * @return {@link GraphNode}
     */
    List<GraphNode> getNextNode();

    List<String> getDependentTable();
}
