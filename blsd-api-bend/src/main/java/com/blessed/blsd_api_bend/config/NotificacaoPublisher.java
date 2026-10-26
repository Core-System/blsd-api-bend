package com.blessed.blsd_api_bend.config;

import com.blessed.blsd_api_bend.dto.agendamento.NotificacaoEmailRequest;
import com.blessed.blsd_api_bend.dto.agendamento.NotificacaoSmsWhatsappRequest;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


@Component
public class NotificacaoPublisher {

    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE_NAME = "notification.exchange";
    private static final String ROUTING_KEY_EMAIL = "notification.email";
    private static final String ROUTING_KEY_SMS = "notification.sms";
    private static final String ROUTING_KEY_WHATSAPP = "notification.whatsapp";

    public NotificacaoPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarEmail(NotificacaoEmailRequest request) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_EMAIL, request);
    }

    public void enviarSms(NotificacaoSmsWhatsappRequest request) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_SMS, request);
    }

    public void enviarWhatsapp(NotificacaoSmsWhatsappRequest request) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_WHATSAPP, request);
    }
}