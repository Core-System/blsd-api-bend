package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.config.NotificacaoCliente;
import com.blessed.blsd_api_bend.dto.agendamento.AttendeeDTO;
import com.blessed.blsd_api_bend.dto.agendamento.CalComRequisicaoDTO;
import com.blessed.blsd_api_bend.dto.agendamento.NotificacaoEmailRequest;
import com.blessed.blsd_api_bend.dto.agendamento.NotificacaoSmsWhatsappRequest;
import com.blessed.blsd_api_bend.model.entity.Consulta;
import com.blessed.blsd_api_bend.model.enums.StatusConsulta;
import com.blessed.blsd_api_bend.repository.ClienteRepository;
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
    private final ClienteRepository clienteRepository;
    private final NotificacaoCliente notificacaoCliente;

    public CalComService(ConsultaRepository consultaRepository,
                         ClienteRepository clienteRepository,
                         NotificacaoCliente notificacaoCliente) {
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
        agendamento.setEventTypeId(5805309);
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
                clienteRepository.findByEmail(email).ifPresentOrElse(cliente -> {

                    Consulta consulta = new Consulta();
                    consulta.setDataHoraInicio(dataHoraInicio);
                    consulta.setDataHoraFim(dataHoraFim);
                    consulta.setCliente(cliente);
                    consulta.setStatusConsulta(StatusConsulta.PENDENTE);

                    consultaRepository.save(consulta);

                    NotificacaoSmsWhatsappRequest requestSms = new NotificacaoSmsWhatsappRequest(
                            "Sistema Blessed7", 11, "989977147",
                            "Olá, " + cliente.getNome() + ", seu agendamento foi realizado com sucesso!"
                    );
                    NotificacaoEmailRequest emailRequest = new NotificacaoEmailRequest(
                            cliente.getEmail(),
                            "Agendamento no Studio Blessed7",
                            "Olá, " + cliente.getNome() + ", seu agendamento foi realizado com sucesso!"
                    );

                    try {
                        notificacaoCliente.enviarSms(requestSms);
                        notificacaoCliente.enviarWhatsapp(requestSms);
                        notificacaoCliente.enviarEmail(emailRequest);
                    } catch (Exception e) {
                        System.err.println("Notificações falharam: " + e.getMessage());
                    }

                }, () -> System.out.println("Cliente não encontrado: " + email));
            }

            return "Sucesso: " + response.getStatusCode();

        } catch (HttpClientErrorException.BadRequest e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Este horário já está reservado. Por favor, escolha outro.");
        }
    }
}