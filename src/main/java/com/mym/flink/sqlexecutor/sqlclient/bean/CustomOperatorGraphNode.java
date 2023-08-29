package com.mym.flink.sqlexecutor.sqlclient.bean;

import com.mym.flink.sqlexecutor.sqlclient.enumtype.GraphNodeOptions;
import com.mym.flink.sqlexecutor.sqlclient.graph.GraphNode;

import java.util.ArrayList;
import java.util.List;

public class CustomOperatorGraphNode implements GraphNode {

    private String nodeName;

    private String desc;

    private String temporaryTableName;

    private CustomOperator customOperator;

    private final List<GraphNode> lastNodeList = new ArrayList<>();

    private final List<GraphNode> nextNodeList = new ArrayList<>();

    private List<String> dependentTable = new ArrayList<>();

    public CustomOperatorGraphNode(String nodeName, String desc, String temporaryTableName, CustomOperator customOperator) {
        this.nodeName = nodeName;
        this.desc = desc;
        this.temporaryTableName = temporaryTableName;
        this.customOperator = customOperator;
    }

    public CustomOperatorGraphNode(String nodeName, String desc, String temporaryTableName) {
        this.nodeName = nodeName;
        this.desc = desc;
        this.temporaryTableName = temporaryTableName;
    }

    public CustomOperatorGraphNode() {
    }

    @Override
    public GraphNodeOptions getGraphNodeOptions() {
        return GraphNodeOptions.CODE_OPERATOR;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    @Override
    public String getTemporaryTableName() {
        return temporaryTableName;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public List<GraphNode> getLastNode() {
        return lastNodeList;
    }

    @Override
    public List<GraphNode> getNextNode() {
        return nextNodeList;
    }

    public CustomOperator getCustomOperator() {
        return customOperator;
    }

    public void setCustomOperator(CustomOperator customOperator) {
        this.customOperator = customOperator;
    }

    public void addLastNode(GraphNode lastNode) {
        this.lastNodeList.add(lastNode);
    }

    public void addNextNode(GraphNode nextNode) {
        this.nextNodeList.add(nextNode);
    }

    public List<String> getDependentTable() {
        return dependentTable;
    }

    @Override
    public String toString() {
        return "CustomOperatorGraphNode{" +
                "nodeName='" + nodeName + '\'' +
                ", desc='" + desc + '\'' +
                ", temporaryTableName='" + temporaryTableName + '\'' +
                ", customOperator=" + customOperator +
                ", dependentTable=" + dependentTable +
                '}';
    }
}
