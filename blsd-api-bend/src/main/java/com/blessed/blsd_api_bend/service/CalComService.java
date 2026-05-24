package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.config.NotificacaoCliente;
import com.blessed.blsd_api_bend.dto.agendamento.AttendeeDTO;
import com.blessed.blsd_api_bend.dto.agendamento.CalComRequisicaoDTO;
import com.blessed.blsd_api_bend.dto.agendamento.NotificacaoEmailRequest;
import com.blessed.blsd_api_bend.dto.agendamento.NotificacaoSmsWhatsappRequest;
import com.blessed.blsd_api_bend.model.entity.ClienteAgendamento;
import com.blessed.blsd_api_bend.model.entity.ConsultaAgendamento;
import com.blessed.blsd_api_bend.repository.ClienteAgendamentoRepository;
import com.blessed.blsd_api_bend.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Service
public class CalComService {

    @Value("${calcom.api.key}")
    private String apiKey;
    private final ConsultaRepository consultaRepository;
    private final ClienteAgendamentoRepository clienteRepository;
    private final NotificacaoCliente notificacaoCliente;

    public CalComService(ConsultaRepository consultaRepository, ClienteAgendamentoRepository clienteRepository, NotificacaoCliente notificacaoCliente) {
        this.consultaRepository = consultaRepository;
        this.clienteRepository = clienteRepository;
        this.notificacaoCliente = notificacaoCliente;
    }

    public String criarAgendamento(String nome, String email, String inicio) {
        RestTemplate restTemplate = new RestTemplate();
        String endpoint = "https://api.cal.com/v2/bookings";

        AttendeeDTO attendee = new AttendeeDTO();
        attendee.setName(nome);
        attendee.setEmail(email);

        CalComRequisicaoDTO agendamento = new CalComRequisicaoDTO();
        agendamento.setEventTypeId(5180374);
        agendamento.setStart(inicio);
        agendamento.setAttendee(attendee);

        ZonedDateTime zonedInicio = ZonedDateTime.parse(inicio);
        LocalDateTime dataHoraInicio = zonedInicio.toLocalDateTime();
        LocalDateTime dataHoraFim = dataHoraInicio.plusMinutes(60);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("cal-api-version", "2026-02-25");
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<CalComRequisicaoDTO> entity = new HttpEntity<>(agendamento, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, entity, String.class);


            if (response.getStatusCode().is2xxSuccessful()) {
                ClienteAgendamento clienteEncontrado = clienteRepository.findByEmail(email);

                if (clienteEncontrado != null) {
                    ConsultaAgendamento consulta = new ConsultaAgendamento();
                    consulta.setDataHoraInicio(dataHoraInicio);
                    consulta.setDataHoraFim(dataHoraFim);
                    consulta.setCliente(clienteEncontrado);
                    consultaRepository.save(consulta);

                    NotificacaoSmsWhatsappRequest requestSms = new NotificacaoSmsWhatsappRequest(
                            "Sistema Blessed7",
                            11,
                            "989977147",
                            "Olá, " + clienteEncontrado.getNome() + ", Seu agendamento foi realizado com sucesso!"
                    );

                    NotificacaoEmailRequest emailRequest = new NotificacaoEmailRequest(
                            clienteEncontrado.getEmail(),
                            "Agendamento no Studio site Blessed7",
                            "Olá, " + clienteEncontrado.getNome() + ", Seu agendamento foi realizado com sucesso!"
                    );

                    try {
                        notificacaoCliente.enviarSms(requestSms);
                        notificacaoCliente.enviarWhatsapp(requestSms);
                        notificacaoCliente.enviarEmail(emailRequest);
                    } catch (Exception e) {
                        System.err.println("Aviso: Agendamento criado no Cal.com e salvo no banco, mas houve falha ao enviar as notificações: " + e.getMessage());
                    }
                } else {
                    System.out.println("Aviso: cliente com email " + email + " não encontrado no banco.");
                }
            }

            return "Sucesso no agendamento: " + response.getStatusCode();
        } catch (HttpClientErrorException.BadRequest e) {
            System.err.println("Erro na Cal.com: " + e.getResponseBodyAsString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este horário já está reservado. Por favor, escolha outro.");
        }
        }
    }