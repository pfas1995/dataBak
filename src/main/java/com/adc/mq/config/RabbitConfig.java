package com.adc.mq.config;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties("spring.rabbitmq")
public class RabbitConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitConfig.class);

    private String host;

    private int port;

    private String vhost;

    private String username;

    private String password;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getVhost() {
        return vhost;
    }

    public void setVhost(String vhost) {
        this.vhost = vhost;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static final String EXCHANGE_A = "my-mq-exchange_A";

    public static final String QUEUE_A = "QUEUE_A";

    public static final String ROUTINGKEY_A = "spring-boot-routingKey_A";

    public static final String EXCHANGE_D = "DLX";

    public static final String QUEUE_D = "QUEUE_D";

    public static final String ROUTINGKEY_D = "KEY_D";

    public static final String QUEUE_R = "QUEUE_R";



    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(vhost);
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }




    /**
     * RabbitTemplate 用来发送消息
     * @return
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    //必须是prototype类型
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        return template;
    }


    /**
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     HeadersExchange ：通过添加属性key-value匹配
     DirectExchange:按照routingkey分发到指定队列
     TopicExchange:多关键字匹配
     */
    @Bean("defaultExchange")
    public DirectExchange defaultExchange() {
        return new DirectExchange(EXCHANGE_A);
    }

    /**
     * 构建队列A：名称， 是否持久化
     * @return
     */
    @Bean("queueA")
    public Queue queueA() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", EXCHANGE_D);
        return new Queue(QUEUE_A, true, false, false, args);
    }


    @Bean("deadExchange")
    public FanoutExchange deadExchange() {
        return new FanoutExchange(EXCHANGE_D, true, false);
    }


    @Bean("queueD")
    public Queue deadQueue() {
        return new Queue(QUEUE_D, true);
    }


    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadQueue())
                .to(deadExchange());
    }




    /**
     * binding, 将 DirectExchange 与 Queue 进行绑定
     * @return
     */
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queueA())
                .to(defaultExchange())
                .with(RabbitConfig.ROUTINGKEY_A);
    }


}
