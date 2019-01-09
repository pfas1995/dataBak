package com.adc.mq.service.impl;

import com.adc.mq.config.RabbitConfig;
import com.adc.mq.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProductServiceImpl implements ProductService, RabbitTemplate.ConfirmCallback {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private RabbitTemplate rabbitTemplate;


    @Autowired
    public ProductServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(this);
    }

    /**
     * 发送消息
     * @param msg
     */
    @Override
    public void send(String msg) {
        String uuid = UUID.randomUUID().toString();
        CorrelationData correlationID = new CorrelationData(uuid);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_A,
                RabbitConfig.ROUTINGKEY_A,
                msg, correlationID);
    }

    /**
     * 确认消息
     * @param correlationData
     * @param ack
     * @param s
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String s) {
        logger.info("ConfirmCallback, correlationData = {}, ack = {}, cause = {} " , correlationData, ack, s);

        if (ack) {
            logger.info("消息投递到Exchange成功");
        }
        else {
            logger.info("消息投递到Exchange失败，" + s);
        }
    }
}
