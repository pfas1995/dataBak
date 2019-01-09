package com.adc.mq.listener;


import com.adc.mq.config.RabbitConfig;
import com.adc.mq.dao.TestRepository;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RabbitListener(queues = {RabbitConfig.QUEUE_A})
    public void receieveMessage(Message message, Channel channel) throws IOException {
        byte[] bytes = message.getBody();
        logger.info("接收消息: " + new String(bytes));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);


        jdbcTemplate.query("select * from rbms_cfg_brand", new RowMapper<Object>(){
            @Override
            public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                // resultSet.getString(3);
                System.out.println(resultSet.getString(3));
                return null;
            }
        });

    }


    @RabbitListener(queues = {RabbitConfig.QUEUE_D})
    public void receieveMessageR(Message message, Channel channel) throws IOException {
        byte[] bytes = message.getBody();
        logger.info("从死信队列接收消息: " + new String(bytes));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

    }





}
