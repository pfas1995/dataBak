package com.adc.mq.service.impl;

import com.adc.mq.entity.SyncMessage;
import com.adc.mq.service.DBoptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DBoptServiceImpl implements DBoptService {

    private static final Logger logger = LoggerFactory.getLogger(DBoptServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String OPT_ADD = "add";
    private static final String OPT_UPDATE = "update";
    private static final String OPT_DEL = "del";
    private static final String TIME_STAMP = "timeStamp";


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
        sql = String.format(sql, condition);
        List<Long> recodeTimeStamps = jdbcTemplate.query(sql, new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet resultSet, int i) throws SQLException {
                Long timeStamp = resultSet.getLong(TIME_STAMP);
                return timeStamp;
            }
        });
        Long recodeTimeStamp = recodeTimeStamps.get(0);
        if (recodeTimeStamp < timeStamp) {
            return false;
        }
        return true;
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
        String insertSql = "INSERT INTO %s(%s) VALUES (%s)";
        String updateSql = "UPDATE %s SET %s WHERE %s";
        String delSql = "DELETE FROM %s WHERE %s";
        String sql = "";
        switch (opt) {
            case OPT_ADD:
                String keys = "";
                String values = "";
                for(String key : data.keySet()) {
                    keys = keys + key + ",";
                    values = values + data.get(key)  + ",";
                }
                keys = keys.substring(0, keys.length()-1);
                values = values.substring(0, values.length()-1);
                sql = String.format(insertSql, tableName, keys, values);
                break;
            case OPT_UPDATE:
                String set = "";
                String condition = "";
                for(String key : data.keySet()) {
                    set = set + key + "=" + data.get(key) + ",";
                }
                set = set.substring(0, set.length()-1);
                for(String key : pk.keySet()) {
                    condition = condition + key + "=" + pk.get(key) + " and";
                }
                condition = condition.substring(0, condition.length()-4);
                sql = String.format(updateSql, tableName, set, condition);
                break;
            case OPT_DEL:
                condition = "";
                for(String key : pk.keySet()) {
                    condition = condition + key + "=" + pk.get(key) + " and";
                }
                condition = condition.substring(0, condition.length()-4);
                sql = String.format(delSql, condition);
                break;
        }
        return sql;
    }


    @Override
    public List<Integer> processMessage(List<SyncMessage> syncMessages) {
        int messageIndex = 0;
        List<Integer>  failIndexs = new ArrayList<>();
        Connection connection = null;
        try{
            connection =jdbcTemplate.getDataSource().getConnection();
            connection.setAutoCommit(false);



        }
        catch (Exception e) {
            String error = e.getMessage();
            logger.error(error);
            try{
                connection.rollback();
                connection.setAutoCommit(true);
                connection.close();
            }
            catch (SQLException e1) {

            }

        }

        for(SyncMessage syncMessage:syncMessages) {

            if(!syncData(syncMessage)) {
                failIndexs.add(messageIndex);
            }
            messageIndex++;
        }
        return failIndexs;

    }

    /**
     *
     * @param syncMessage
     * @return
     */
    @Override
    public Boolean syncData(SyncMessage syncMessage) {
        String dataSource = syncMessage.getDbName();
        //Todo 切换数据源

        String opt = syncMessage.getOpType();
        String sql = MessageToSql(opt, syncMessage.getTableName(), null, syncMessage.getData());

        switch (opt) {
            case OPT_ADD:
            case OPT_DEL:
                try {
                    jdbcTemplate.update(sql);
                }
                catch (Exception e) {
                    logger.error(e.getMessage());
                    return false;
                }

                break;
            case OPT_UPDATE:
                Boolean valid = checkValid(syncMessage.getTableName(), syncMessage.getPk(), syncMessage.getTimestamp());
                if(valid) {
                    try {
                        jdbcTemplate.update(sql);
                    }
                    catch (Exception e) {
                        logger.error(e.getMessage());
                        return false;
                    }
                }
        }
        return true;
    }
}
