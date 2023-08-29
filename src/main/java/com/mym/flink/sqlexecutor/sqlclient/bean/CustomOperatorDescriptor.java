package com.mym.flink.sqlexecutor.sqlclient.bean;

import java.util.List;

public class CustomOperatorDescriptor {

    private String name;

    private String desc;

    private CustomOperator customOperator;

    private String resultAsTable;

    private List<String> dependentTable;

    public CustomOperatorDescriptor() {
    }

    public CustomOperatorDescriptor(String name, String desc, CustomOperator customOperator, String resultAsTable, List<String> dependentTable) {
        this.name = name;
        this.desc = desc;
        this.customOperator = customOperator;
        this.resultAsTable = resultAsTable;
        this.dependentTable = dependentTable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public CustomOperator getCustomOperator() {
        return customOperator;
    }

    public void setCustomOperator(CustomOperator customOperator) {
        this.customOperator = customOperator;
    }

    public String getResultAsTable() {
        return resultAsTable;
    }

    public void setResultAsTable(String resultAsTable) {
        this.resultAsTable = resultAsTable;
    }

    public List<String> getDependentTable() {
        return dependentTable;
    }

    public void setDependentTable(List<String> dependentTable) {
        this.dependentTable = dependentTable;
    }

    @Override
    public String toString() {
        return "CustomOperatorDescriptor{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", customOperator=" + customOperator +
                ", resultAsTable='" + resultAsTable + '\'' +
                ", dependentTable=" + dependentTable +
                '}';
    }
}
