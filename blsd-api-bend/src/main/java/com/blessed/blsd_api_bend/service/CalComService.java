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

    public String criarAgendamento(String nome, String email, String inicio, String procedimento) {

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

                    String data = "%02d/%02d/%d".formatted(zonedInicio.getDayOfMonth(), zonedInicio.getMonthValue(), zonedInicio.getYear());

                    String hora = "%02d:%02d".formatted(zonedInicio.getHour(), zonedInicio.getMinute());

                    NotificacaoSmsWhatsappRequest requestSms = new NotificacaoSmsWhatsappRequest(
                            "Sistema Blessed7", 11, "989977147",
                            "Olá, " + cliente.getNome() + ", seu agendamento foi realizado com sucesso!"
                    );
                    NotificacaoEmailRequest emailRequest = new NotificacaoEmailRequest(
                            cliente.getEmail(),
                            "Agendamento no Studio Blessed7",
                            """
                                    <div style="background: rgb(233, 235, 236); padding: 2rem; border-radius: 12px; font-family: 'Poppins', Inter, system-ui, Arial, sans-serif;">
                                              <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet"/>
                                              <div style="max-width: 560px; margin: 0 auto; background: #FFFCF9; border-radius: 12px; border: 0.5px solid #3a5b64; overflow: hidden; font-family: 'Poppins', Inter, system-ui, Arial, sans-serif;">
                                    
                                                <div style="background: #0B3C49; padding: 1.5rem 2rem; display: flex; align-items: center; gap: 10px;">
                                                  <div style="width: 28px; height: 28px; background: #3a5b64; border-radius: 6px; display: flex; align-items: center; justify-content: center;">
                                    
                                                  </div>
                                                  <span style="color: #FFFCF9; font-weight: 600; font-size: 15px; font-family: 'Poppins', Inter, system-ui, Arial, sans-serif;">Blessed7</span>
                                                </div>
                                    
                                                <div style="padding: 2rem;">
                                    
                                                  <p style="font-size: 13px; color: #1a5666; margin: 0 0 1.5rem; font-family: 'Poppins', Inter, system-ui, Arial, sans-serif;">
                                                    Olá, <strong style="color: #0B3C49  ;">%s</strong>
                                                  </p>
                                    
                                                  <h2 style="font-size: 20px; font-weight: 600; color: #0B3C49; margin: 0 0 0.75rem; line-height: 1.4; font-family: 'Poppins', Inter, system-ui, Arial, sans-serif;">
                                                    Seu agendamento foi confirmado!
                                                  </h2>
                                    
                                                  <p style="font-size: 14px; color: #1a5666; line-height: 1.7; margin: 0 0 1.5rem; font-family: 'Poppins', Inter, system-ui, Arial, sans-serif;">
                                                    Prepare-se para o seu momento de autocuidado. Aqui estão os detalhes do seu horário no Blessed7:
                                                  </p>
                                                  <!-- c0e1ea89 -->
                                                  <div style="background: #FDFFDA; border-radius: 8px; padding: 1.25rem; margin-bottom: 2rem;">
                                                    <p style="font-size: 14px; color: #1a5666; margin: 0 0 0.5rem; font-family: 'Poppins', Inter, system-ui, Arial, sans-serif;">
                                                      <strong style="color: #0B3C49;">Serviço:</strong> %s
                                                    </p>
                                                    <p style="font-size: 14px; color: #1a5666; margin: 0 0 0.5rem; font-family: 'Poppins', Inter, system-ui, Arial, sans-serif;">
                                                      <strong style="color: #0B3C49;">Data:</strong> %s
                                                    </p>
                                                    <p style="font-size: 14px; color: #1a5666; margin: 0; font-family: 'Poppins', Inter, system-ui, Arial, sans-serif;">
                                                      <strong style="color: #0B3C49;">Horário:</strong> %s
                                                    </p>
                                                  </div>
                                    
                                                  <div style="text-align: center; margin: 2rem 0;">
                                                    <a href="%s" style="display: inline-block; background: #0B3C49; color: #FFFCF9; font-size: 14px; font-weight: 600; padding: 12px 32px; border-radius: 12px; text-decoration: none; letter-spacing: 0.01em; font-family: 'Poppins', Inter, system-ui, Arial, sans-serif;">
                                                     Gerenciar Agendamento
                                                    </a>
                                                  </div>
                                    
                                                  <div style="background: #FDFFDA; border-left: 3px solid #0B3C49; border-radius: 0 8px 8px 0; padding: 0.875rem 1rem; margin-bottom: 1.5rem;">
                                                    <p style="font-size: 13px; color: #1a5666; margin: 0; line-height: 1.6; font-family: 'Poppins', Inter, system-ui, Arial, sans-serif;">
                                                      <strong style="color: #0B3C49;">Importante:</strong> Pedimos que chegue com pelo menos <strong style="color: #0B3C49;">10 minutos</strong> de antecedência. Caso precise cancelar ou reagendar, por favor, nos avise o quanto antes.
                                                    </p>
                                                  </div>
                                    
                                                  <p style="font-size: 13px; color: #1a5666; margin: 0 0 0.5rem; font-family: 'Poppins', Inter, system-ui, Arial, sans-serif;">
                                                    Se o botão não funcionar, copie e cole o link abaixo no seu navegador:
                                                  </p>
                                                  <p style="font-size: 12px; color: #0B3C49; background: #FDFFDA; padding: 10px 12px; border-radius: 8px; word-break: break-all; margin: 0 0 1.5rem; font-family: monospace;">
                                                    %s
                                                  </p>
                                    
                                                  <p style="font-size: 13px; color: #1a5666; margin: 0 0 1.5rem; line-height: 1.6; text-align: center; font-family: 'Poppins', Inter, system-ui, Arial, sans-serif;">
                                                    Estamos ansiosas para te ver!
                                                  </p>
                                    
                                                  <hr style="border: none; border-top: 1px solid #3a5b64; margin: 1.5rem 0;" />
                                    
                                                  <p style="font-size: 12px; color: #1a5666; margin: 0; line-height: 1.6; text-align: center; font-family: 'Poppins', Inter, system-ui, Arial, sans-serif;">
                                                    © 2026 Blessed7. Todos os direitos reservados.<br>
                                                    Este é um e-mail automático, por favor não responda.
                                                  </p>
                                                </div>
                                              </div>
                                            </div>
                                    """.formatted(cliente.getNome(), procedimento, data, hora, cliente.getNome(), cliente.getNome())
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