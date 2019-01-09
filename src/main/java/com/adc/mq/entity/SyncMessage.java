package com.adc.mq.entity;

import java.util.Map;

public class SyncMessage {

    private String dbName;
    private String opType;
    private String tableName;
    private Long timestamp;
    private Map<String, Object> pk;
    private Map<String, Object> data;

    public SyncMessage(String dbName, String opType, String tableName, Long timestamp, Map data) {
        this.dbName = dbName;
        this.opType = opType;
        this.tableName = tableName;
        this.timestamp = timestamp;
        this.data = data;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getPk() {
        return pk;
    }

    public void setPk(Map<String, Object> pk) {
        this.pk = pk;
    }

    @Override
    public String toString() {
        return "SyncMessage{" +
                "dbName='" + dbName + '\'' +
                ", opType='" + opType + '\'' +
                ", tableName='" + tableName + '\'' +
                ", timestamp=" + timestamp +
                ", data=" + data +
                '}';
    }
}
