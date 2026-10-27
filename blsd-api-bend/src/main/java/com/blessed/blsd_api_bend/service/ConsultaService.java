package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.config.clientes.NotificacaoCliente;
import com.blessed.blsd_api_bend.dto.agendamento.AgendamentoRequestDTO;
import com.blessed.blsd_api_bend.dto.agendamento.NotificacaoEmailRequest;
import com.blessed.blsd_api_bend.dto.agendamento.NotificacaoSmsWhatsappRequest;
import com.blessed.blsd_api_bend.dto.agendamento.ReservaDataDTO;
import com.blessed.blsd_api_bend.dto.consulta.*;
import com.blessed.blsd_api_bend.exception.agendamento.HorarioIndisponivelException;
import com.blessed.blsd_api_bend.model.entity.*;
import com.blessed.blsd_api_bend.model.enums.StatusConsulta;
import com.blessed.blsd_api_bend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final ClienteRepository clienteRepository;
    private final ServicoRepository servicoRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final CalComService calComService;
    private final NotificacaoCliente notificacaoCliente;

    public ConsultaService(ConsultaRepository consultaRepository,
                           ClienteRepository clienteRepository,
                           ServicoRepository servicoRepository,
                           AvaliacaoRepository avaliacaoRepository,
                           CalComService calComService,
                           NotificacaoCliente notificacaoCliente) {
        this.consultaRepository = consultaRepository;
        this.clienteRepository = clienteRepository;
        this.servicoRepository = servicoRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.calComService = calComService;
        this.notificacaoCliente = notificacaoCliente;
    }

    public List<ConsultaResponseDTO> listarPorCliente(Long clienteId) {
        return consultaRepository.findByClienteId(clienteId)
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public ConsultaResponseDTO criarAgendamento(AgendamentoRequestDTO request) {
        ZonedDateTime zonedInicio = ZonedDateTime.parse(request.getDataHoraInicio());
        LocalDateTime dataHoraInicio = zonedInicio.toLocalDateTime();
        LocalDateTime dataHoraFim = dataHoraInicio.plusMinutes(60);

        List<Consulta> conflitos = consultaRepository.findConsultasConflitantes(dataHoraInicio, dataHoraFim);
        if (!conflitos.isEmpty()) {
            throw new HorarioIndisponivelException("Já existe um agendamento registrado em nosso sistema para este horário.");
        }

        String bookingUid = calComService.solicitarAgendamento(request.getNome(), request.getEmail(), request.getDataHoraInicio());

        Cliente cliente = clienteRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + request.getEmail()));

        Servico servicoEscolhido = servicoRepository.findByNome(request.getProcedimento())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        Consulta consulta = new Consulta();
        consulta.setDataHoraInicio(dataHoraInicio);
        consulta.setDataHoraFim(dataHoraFim);
        consulta.setCliente(cliente);
        consulta.setStatusConsulta(StatusConsulta.PENDENTE);
        consulta.setServicos(List.of(servicoEscolhido));
        consulta.setCalBookingUid(bookingUid);

        consultaRepository.save(consulta);

        cliente.gerarToken();
        clienteRepository.save(cliente);

//        dispararNotificacoes(cliente, request.getProcedimento(), zonedInicio);

        return toDTO(consulta);
    }

    private void dispararNotificacoes(Cliente cliente, String procedimento, ZonedDateTime zonedInicio) {
        try {
            String data = "%02d/%02d/%d".formatted(zonedInicio.getDayOfMonth(), zonedInicio.getMonthValue(), zonedInicio.getYear());
            String hora = "%02d:%02d".formatted(zonedInicio.getHour(), zonedInicio.getMinute());
            String linkAgendamento = "http://localhost:5173/agendar?code=%s".formatted(cliente.getTokenAgendamento());

            NotificacaoSmsWhatsappRequest requestSms = new NotificacaoSmsWhatsappRequest(
                    "Sistema Blessed7", 11, "989977147",
                    "Olá, " + cliente.getNome() + ", seu agendamento foi realizado com sucesso!"
            );

            NotificacaoEmailRequest emailRequest = new NotificacaoEmailRequest(
                    cliente.getEmail(),
                    "Agendamento no Studio Blessed7",
                    """
                    <div style="background: rgb(233, 235, 236); padding: 2rem; border-radius: 12px; font-family: 'Poppins', sans-serif;">
                      <div style="max-width: 560px; margin: 0 auto; background: #FFFCF9; border-radius: 12px; border: 0.5px solid #3a5b64; overflow: hidden;">
                        <div style="background: #0B3C49; padding: 1.5rem 2rem;">
                          <span style="color: #FFFCF9; font-weight: 600; font-size: 15px;">Blessed7</span>
                        </div>
                        <div style="padding: 2rem;">
                          <p style="font-size: 13px; color: #1a5666;">Olá, <strong style="color: #0B3C49;">%s</strong></p>
                          <h2 style="font-size: 20px; font-weight: 600; color: #0B3C49;">Seu agendamento foi confirmado!</h2>
                          <div style="background: #FDFFDA; border-radius: 8px; padding: 1.25rem; margin-bottom: 2rem;">
                            <p style="font-size: 14px; color: #1a5666;"><strong>Serviço:</strong> %s</p>
                            <p style="font-size: 14px; color: #1a5666;"><strong>Data:</strong> %s</p>
                            <p style="font-size: 14px; color: #1a5666;"><strong>Horário:</strong> %s</p>
                          </div>
                          <div style="text-align: center;">
                            <a href="%s" style="background: #0B3C49; color: #FFFCF9; padding: 12px 32px; border-radius: 12px; text-decoration: none;">Gerenciar Agendamento</a>
                          </div>
                        </div>
                      </div>
                    </div>
                    """.formatted(cliente.getNome(), procedimento, data, hora, linkAgendamento)
            );

            notificacaoCliente.enviarSms(requestSms);
            notificacaoCliente.enviarWhatsapp(requestSms);
            notificacaoCliente.enviarEmail(emailRequest);
        } catch (Exception e) {
            System.err.println("Aviso: Falha ao enviar notificações, mas o agendamento foi concluído: " + e.getMessage());
        }
    }

    @Transactional
    public ConsultaResponseDTO avaliar(Long consultaId, AvaliacaoRequestDTO dto) {
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        if (consulta.getStatusConsulta() != StatusConsulta.CONCLUIDA) {
            throw new RuntimeException("Só é possível avaliar consultas concluídas");
        }

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNota(dto.getNota());
        avaliacao.setDescricao(dto.getDescricao());
        avaliacao.setCliente(consulta.getCliente());
        avaliacaoRepository.save(avaliacao);

        consulta.setAvaliacao(avaliacao);
        consultaRepository.save(consulta);

        return toDTO(consulta);
    }

    private ConsultaResponseDTO toDTO(Consulta c) {
        AvaliacaoDTO avaliacaoDTO = null;
        if (c.getAvaliacao() != null) {
            avaliacaoDTO = new AvaliacaoDTO(
                    c.getAvaliacao().getId(),
                    c.getAvaliacao().getNota(),
                    c.getAvaliacao().getDescricao()
            );
        }
        return new ConsultaResponseDTO(
                c.getId(),
                c.getDataHoraInicio(),
                c.getDataHoraFim(),
                c.getStatusConsulta() != null ? c.getStatusConsulta().name() : "PENDENTE",
                c.getServicos() != null
                        ? c.getServicos().stream().map(Servico::getNome).toList()
                        : List.of(),
                avaliacaoDTO, c.getCalBookingUid()
        );
    }

    public ReservaDataDTO buscarDetalhesExternos(Long consultaId) {
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        if (consulta.getCalBookingUid() == null) {
            throw new RuntimeException("Esta consulta não possui vinculo com o Cal.com");
        }

        return calComService.buscarDetalhesReserva(consulta.getCalBookingUid());
    }
}