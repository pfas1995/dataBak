package com.adc.mq.entity;

import java.util.Map;

public class SyncMessage {

    public String dbName;
    public String opType;
    public String tableName;
    public Long timestamp;
    public Map<String, Object> data;

    public SyncMessage(String dbName, String opType, String tableName, Long timestamp, Map data) {
        this.dbName = dbName;
        this.opType = opType;
        this.tableName = tableName;
        this.timestamp = timestamp;
        this.data = data;
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
