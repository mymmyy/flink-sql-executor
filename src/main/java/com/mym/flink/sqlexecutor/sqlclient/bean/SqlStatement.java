package com.mym.flink.sqlexecutor.sqlclient.bean;

import java.util.UUID;

public class SqlStatement {

    private String id;

    private String sql;

    private String alias;

    @Override
    public String toString() {
        return "SqlStatement{" +
                "id='" + id + '\'' +
                ", sql='" + sql + '\'' +
                ", alias='" + alias + '\'' +
                '}';
    }

    public SqlStatement() {
    }

    public SqlStatement(String id, String sql, String alias) {
        this.id = id;
        this.sql = sql;
        this.alias = alias;
    }

    public SqlStatement(String sql) {
        this(String.valueOf(UUID.randomUUID()), sql, "");
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getId() {
        return id;
    }

    public String getSql() {
        return sql;
    }

    public String getAlias() {
        return alias;
    }
}
