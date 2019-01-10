package com.adc.mq.listener;


import com.adc.mq.config.RabbitConfig;
import com.adc.mq.entity.SyncMessage;
import com.adc.mq.service.DBoptService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);
    private int fireNumber = 0;

    @Autowired
    private DBoptService dBoptService;

    /**
     * 消息队列监听器
     * 监听消息，并把入库
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = {RabbitConfig.QUEUE_A})
    public void receieveMessage(Message message, Channel channel) throws IOException {
        byte[] bytes = message.getBody();

        Gson gson = new Gson();
        List<SyncMessage> syncMessages =  gson.fromJson(new String(bytes), new TypeToken<List<SyncMessage>>(){}.getType());
        try{
            dBoptService.processMessage(syncMessages);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//            fireNumber++;
//            System.out.println(fireNumber + "," + System.currentTimeMillis());
        }
        catch (Exception e) {
            //Todo 错误处理
            logger.error(e.getMessage());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }


    @RabbitListener(queues = {RabbitConfig.QUEUE_D})
    public void receieveMessageR(Message message, Channel channel) throws IOException {
        byte[] bytes = message.getBody();
        logger.info("从死信队列接收消息: " + new String(bytes));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

    }





}