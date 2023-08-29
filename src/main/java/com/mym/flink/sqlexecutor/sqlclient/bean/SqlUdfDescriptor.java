package com.mym.flink.sqlexecutor.sqlclient.bean;

import com.mym.flink.sqlexecutor.sqlclient.enumtype.ColumnType;

import java.util.LinkedHashMap;

public class SqlUdfDescriptor {

    private String callName;

    private String classFullName;

    private LinkedHashMap<String, ColumnType> argsMap;

    private ColumnType returnType;

    private String desc;

    public SqlUdfDescriptor() {
    }

    public String getCallName() {
        return callName;
    }

    public void setCallName(String callName) {
        this.callName = callName;
    }

    public LinkedHashMap<String, ColumnType> getArgsMap() {
        return argsMap;
    }

    public void setArgsMap(LinkedHashMap<String, ColumnType> argsMap) {
        this.argsMap = argsMap;
    }

    public ColumnType getReturnType() {
        return returnType;
    }

    public void setReturnType(ColumnType returnType) {
        this.returnType = returnType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getClassFullName() {
        return classFullName;
    }

    public void setClassFullName(String classFullName) {
        this.classFullName = classFullName;
    }

    @Override
    public String toString() {
        return "SqlUdfDescriptor{" +
                "callName='" + callName + '\'' +
                ", classFullName='" + classFullName + '\'' +
                ", argsMap=" + argsMap +
                ", returnType=" + returnType +
                ", desc='" + desc + '\'' +
                '}';
    }
}


