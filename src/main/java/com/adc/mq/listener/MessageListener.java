package com.adc.mq.listener;


import com.adc.mq.config.RabbitConfig;
import com.adc.mq.entity.SyncMessage;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);

    @RabbitListener(queues = {RabbitConfig.QUEUE_A})
    public void receieveMessage(Message message, Channel channel) throws IOException {
        byte[] bytes = message.getBody();
        logger.info("接收消息: " + new String(bytes));

        Gson gson = new Gson();
        SyncMessage syncMessage = gson.fromJson(new String(bytes), SyncMessage.class);
        logger.info(syncMessage.toString());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    @RabbitListener(queues = {RabbitConfig.QUEUE_D})
    public void receieveMessageR(Message message, Channel channel) throws IOException {
        byte[] bytes = message.getBody();
        logger.info("从死信队列接收消息: " + new String(bytes));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

    }





}
