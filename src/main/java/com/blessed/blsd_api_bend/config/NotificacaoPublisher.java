package com.blessed.blsd_api_bend.config;

import com.blessed.blsd_api_bend.dto.agendamento.NotificacaoEmailRequest;
import com.blessed.blsd_api_bend.dto.agendamento.NotificacaoSmsWhatsappRequest;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


@Component
public class NotificacaoPublisher {

    private final RabbitTemplate rabbitTemplate;

    public NotificacaoPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarEmail(NotificacaoEmailRequest request) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_EMAIL, request);
    }

    public void enviarSms(NotificacaoSmsWhatsappRequest request) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_SMS, request);
    }

    public void enviarWhatsapp(NotificacaoSmsWhatsappRequest request) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_WHATSAPP, request);
    }
}