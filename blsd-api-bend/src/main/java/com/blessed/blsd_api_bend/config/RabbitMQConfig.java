package com.blessed.blsd_api_bend.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "notification.exchange";
    public static final String QUEUE_EMAIL = "notification.email.queue";
    public static final String QUEUE_SMS = "notification.sms.queue";
    public static final String QUEUE_WHATSAPP = "notification.whatsapp.queue";
    public static final String ROUTING_KEY_EMAIL = "notification.email";
    public static final String ROUTING_KEY_SMS = "notification.sms";
    public static final String ROUTING_KEY_WHATSAPP = "notification.whatsapp";

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(QUEUE_EMAIL, true);
    }

    @Bean
    public Queue smsQueue() {
        return new Queue(QUEUE_SMS, true);
    }

    @Bean
    public Queue whatsappQueue() {
        return new Queue(QUEUE_WHATSAPP, true);
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(emailQueue).to(notificationExchange).with(ROUTING_KEY_EMAIL);
    }

    @Bean
    public Binding smsBinding(Queue smsQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(smsQueue).to(notificationExchange).with(ROUTING_KEY_SMS);
    }

    @Bean
    public Binding whatsappBinding(Queue whatsappQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(whatsappQueue).to(notificationExchange).with(ROUTING_KEY_WHATSAPP);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
