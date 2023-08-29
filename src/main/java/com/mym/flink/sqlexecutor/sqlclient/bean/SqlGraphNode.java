package com.mym.flink.sqlexecutor.sqlclient.bean;

import com.mym.flink.sqlexecutor.sqlclient.enumtype.GraphNodeOptions;
import com.mym.flink.sqlexecutor.sqlclient.graph.GraphNode;

import java.util.ArrayList;
import java.util.List;

public class SqlGraphNode implements GraphNode {

    public SqlGraphNode() {
    }

    public SqlGraphNode(String nodeName, String desc, String temporaryTableName, SqlStatement statement) {
        this.nodeName = nodeName;
        this.desc = desc;
        this.temporaryTableName = temporaryTableName;
        this.statement = statement;
    }

    private String nodeName;

    private String desc;

    private String temporaryTableName;

    private SqlStatement statement;

    private final List<GraphNode> lastNodeList = new ArrayList<>();

    private final List<GraphNode> nextNodeList = new ArrayList<>();

    private boolean isSourceNode = false;

    private boolean isSinkNode = false;

    private List<String> dependentTable = new ArrayList<>();


    @Override
    public GraphNodeOptions getGraphNodeOptions() {
        return GraphNodeOptions.SQL;
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

    public SqlStatement getStatement() {
        return statement;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setTemporaryTableName(String temporaryTableName) {
        this.temporaryTableName = temporaryTableName;
    }

    public void setStatement(SqlStatement statement) {
        this.statement = statement;
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
        return "SqlGraphNode{" +
                "nodeName='" + nodeName + '\'' +
                ", desc='" + desc + '\'' +
                ", temporaryTableName='" + temporaryTableName + '\'' +
                ", statement=" + statement +
                ", isSourceNode=" + isSourceNode +
                ", isSinkNode=" + isSinkNode +
                ", dependentTable=" + dependentTable +
                '}';
    }
}
