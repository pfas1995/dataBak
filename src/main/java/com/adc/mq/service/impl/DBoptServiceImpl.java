package com.adc.mq.service.impl;

import com.adc.mq.entity.SyncMessage;
import com.adc.mq.service.DBoptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
public class DBoptServiceImpl implements DBoptService {

    private static final Logger logger = LoggerFactory.getLogger(DBoptServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    @Autowired
//    private TransactionTemplate transactionTemplate;

    public static final String OPT_ADD = "add";
    public static final String OPT_UPDATE = "update";
    public static final String OPT_DEL = "del";
    public static final String TIME_STAMP = "time";



    /**
     * 检测时间戳，判定同步消息是否有效
     * @param tableName
     * @param pk
     * @param timeStamp
     * @return
     */
    private Boolean checkValid(String tableName, Map<String, Object> pk, Long timeStamp) {
        String sql = "SELECT * FROM %s WHERE %s";
        String condition = "";
        for(String key : pk.keySet()) {
            condition = condition + key + "=" + pk.get(key) + " and";
        }
        condition = condition.substring(0, condition.length()-4);
        sql = String.format(sql, tableName, condition);
        List<Long> recodeTimeStamps = jdbcTemplate.query(sql, new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet resultSet, int i) throws SQLException {
                Long timeStamp = resultSet.getLong(TIME_STAMP);
                return timeStamp;
            }
        });
        if (recodeTimeStamps == null || recodeTimeStamps.isEmpty()) {
            return false;
        }

        Long recodeTimeStamp = recodeTimeStamps.get(0);
        if (recodeTimeStamp > timeStamp) {
            return false;
        }
        return true;
    }


    /**
     * 依据对象类型转化成相应的 sql 语句中的语法
     * @param o
     * @return
     */
    private Object convertValue(Object o) {
        if(o instanceof String) {
            return "'" + o + "'";
        }
        else {
            return o;
        }
    }

    /**
     * 将 SyncMessage 转化成 sql 语句
     * @param opt
     * @param tableName
     * @param pk
     * @param data
     * @return
     */
    private String MessageToSql(String opt, String tableName, Map<String, Object> pk, Map<String, Object> data) {
        String insertSql = "INSERT INTO %s (%s) VALUES (%s)";
        String updateSql = "UPDATE %s SET %s WHERE %s";
        String delSql = "DELETE FROM %s WHERE %s";
        String sql = "";
        switch (opt) {
            case OPT_ADD:
                String keys = "";
                String values = "";
                for(String key : data.keySet()) {
                    keys = keys + key + ",";
                    values = values + convertValue(data.get(key))  + ",";
                }
                keys = keys.substring(0, keys.length()-1);
                values = values.substring(0, values.length()-1);
                sql = String.format(insertSql, tableName, keys, values);
                break;
            case OPT_UPDATE:
                String set = "";
                String condition = "";
                for(String key : data.keySet()) {
                    if (pk.containsKey(key)) {
                        continue;
                    }
                    set = set + key + "=" + convertValue(data.get(key)) + ",";

                }
                set = set.substring(0, set.length()-1);
                for(String key : pk.keySet()) {
                    condition = condition + key + "=" + convertValue(pk.get(key)) + " and";
                }
                condition = condition.substring(0, condition.length()-4);
                sql = String.format(updateSql, tableName, set, condition);
                break;
            case OPT_DEL:
                condition = "";
                for(String key : pk.keySet()) {
                    condition = condition + key + "=" + convertValue(pk.get(key)) + " and";
                }
                condition = condition.substring(0, condition.length()-4);
                sql = String.format(delSql, tableName, condition);
                break;
        }
        return sql;
    }


    /**
     * 处理消息
     * @param syncMessages
     * @return
     */
    @Override
    @Transactional
    public void processMessage(List<SyncMessage> syncMessages) {
        logger.info(syncMessages.toString());
        for(SyncMessage syncMessage : syncMessages) {
            this.syncData(syncMessage);
        }


    }

    /**
     * 同步消息中的一条数据
     * @param syncMessage
     * @return
     */
    @Override
    public Boolean syncData(SyncMessage syncMessage)  {
        Boolean success = false;
        String dataSource = syncMessage.getDbName();
        //Todo 切换数据源
        Map<String, Object> data = syncMessage.getData();
        data.put(TIME_STAMP, syncMessage.getTimestamp());

        String opt = syncMessage.getOpType();

        String sql = MessageToSql(opt, syncMessage.getTableName(), syncMessage.getPk(), data);
        logger.info(sql);
        try {
            switch (opt) {
                case OPT_ADD:
                case OPT_DEL:
                    jdbcTemplate.update(sql);
                    break;
                case OPT_UPDATE:
                    Boolean valid = checkValid(syncMessage.getTableName(), syncMessage.getPk(), syncMessage.getTimestamp());
                    if(valid) {
                        jdbcTemplate.update(sql);
                    }
            }
            success = true;
        }
        catch (Exception e) {
            success = false;
            throw e;
        }
        return success;
    }
}
