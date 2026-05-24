package com.blessed.blsd_api_bend.dto.agendamento;

public record NotificacaoSmsWhatsappRequest(String sender, int ddd, String phoneNumber, String message) {
}