package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.config.NotificacaoPublisher;
import com.blessed.blsd_api_bend.dto.agendamento.AgendamentoRequestDTO;
import com.blessed.blsd_api_bend.dto.agendamento.NotificacaoEmailRequest;
import com.blessed.blsd_api_bend.dto.agendamento.NotificacaoSmsWhatsappRequest;
import com.blessed.blsd_api_bend.dto.agendamento.ReservaDataDTO;
import com.blessed.blsd_api_bend.dto.consulta.*;
import com.blessed.blsd_api_bend.exception.agendamento.HorarioIndisponivelException;
import com.blessed.blsd_api_bend.exception.consulta.ConsultaNaoConcluidaException;
import com.blessed.blsd_api_bend.exception.consulta.ConsultaNotFoundException;
import com.blessed.blsd_api_bend.exception.consulta.ReservaExternaNotFoundException;
import com.blessed.blsd_api_bend.exception.consulta.ConsultaStatusInvalidoException;
import com.blessed.blsd_api_bend.model.entity.*;
import com.blessed.blsd_api_bend.model.enums.StatusConsulta;
import com.blessed.blsd_api_bend.model.enums.LocalConsulta;
import com.blessed.blsd_api_bend.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.data.domain.PageImpl;

@Service
@Slf4j
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final ClienteRepository clienteRepository;
    private final ServicoRepository servicoRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final CalComService calComService;
    private final NotificacaoPublisher notificacaoPublisher;

    @org.springframework.beans.factory.annotation.Value("${calcom.time-zone:America/Sao_Paulo}")
    private String timeZone;

    public ConsultaService(ConsultaRepository consultaRepository,
                           ClienteRepository clienteRepository,
                           ServicoRepository servicoRepository,
                           AvaliacaoRepository avaliacaoRepository,
                           CalComService calComService,
                           NotificacaoPublisher notificacaoPublisher) {
        this.consultaRepository = consultaRepository;
        this.clienteRepository = clienteRepository;
        this.servicoRepository = servicoRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.calComService = calComService;
        this.notificacaoPublisher = notificacaoPublisher;
    }

    public List<ConsultaResponseDTO> listarPorCliente(Long clienteId) {
        return consultaRepository.findByClienteId(clienteId)
                .stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public Page<ConsultaResponseDTO> listarTodos(StatusConsulta status,
                                                  String busca,
                                                  String procedimento,
                                                  LocalDateTime inicio,
                                                  LocalDateTime fim,
                                                  Pageable pageable) {
        String buscaNormalizada = busca == null || busca.isBlank() ? null : busca.trim();
        String procedimentoNormalizado = procedimento == null || procedimento.isBlank() ? null : procedimento.trim();
        return consultaRepository.buscar(status, buscaNormalizada, procedimentoNormalizado, inicio, fim, pageable)
                .map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<HorarioDisponivelDTO> listarHorariosDisponiveis(LocalDate data, Pageable pageable) {
        List<HorarioDisponivelDTO> horarios = calComService.buscarHorariosDisponiveis(data);
        int inicio = (int) Math.min(pageable.getOffset(), horarios.size());
        int fim = Math.min(inicio + pageable.getPageSize(), horarios.size());
        List<HorarioDisponivelDTO> pagina = horarios.subList(inicio, fim);
        return new PageImpl<>(pagina, pageable, horarios.size());
    }

    @Transactional
    public ConsultaResponseDTO criarAgendamento(AgendamentoRequestDTO request) {
        if (request.getLocalConsulta() == LocalConsulta.DOMICILIO) {
            throw new IllegalArgumentException("O atendimento em domicílio está temporariamente indisponível.");
        }

        ZonedDateTime zonedInicio = ZonedDateTime.parse(request.getDataHoraInicio());
        LocalDateTime dataHoraInicio = zonedInicio
                .withZoneSameInstant(ZoneId.of(timeZone))
                .toLocalDateTime();
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
        consulta.setLocalConsulta(request.getLocalConsulta());
        consulta.setCalBookingUid(bookingUid);

        consultaRepository.save(consulta);

        cliente.gerarToken();
        clienteRepository.save(cliente);

        try {
            dispararNotificacoes(cliente, request.getProcedimento(), dataHoraInicio, request.getLocalConsulta());
        } catch (IllegalStateException exception) {
            log.error("Agendamento {} salvo, mas as notificações não foram publicadas", consulta.getId(), exception);
        }

        return toDTO(consulta);
    }

    private void dispararNotificacoes(Cliente cliente, String procedimento, LocalDateTime dataHoraInicio, LocalConsulta localConsulta) {
        try {
            String data = "%02d/%02d/%d".formatted(dataHoraInicio.getDayOfMonth(), dataHoraInicio.getMonthValue(), dataHoraInicio.getYear());
            String hora = "%02d:%02d".formatted(dataHoraInicio.getHour(), dataHoraInicio.getMinute());
            String linkAgendamento = "http://localhost:5173/agendar?code=%s".formatted(cliente.getTokenAgendamento());

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
                            <p style="font-size: 14px; color: #1a5666;"><strong>Local:</strong> Clínica — Rua Entre-Folhas, 4a - Jardim Arize</p>
                          </div>
                          <div style="text-align: center;">
                            <a href="%s" style="background: #0B3C49; color: #FFFCF9; padding: 12px 32px; border-radius: 12px; text-decoration: none;">Gerenciar Agendamento</a>
                          </div>
                        </div>
                      </div>
                    </div>
                    """.formatted(cliente.getNome(), procedimento, data, hora, linkAgendamento)
            );

            notificacaoPublisher.enviarEmail(emailRequest);
            String telefone = cliente.getTelefone();
            if (telefone != null && !telefone.isBlank()) {
                String telefoneNumeros = telefone.replaceAll("\\D", "");
                if (telefoneNumeros.length() >= 10) {
                    int ddd = Integer.parseInt(telefoneNumeros.substring(0, 2));
                    String numero = telefoneNumeros.substring(2);
                    NotificacaoSmsWhatsappRequest requestSms = new NotificacaoSmsWhatsappRequest(
                            "Sistema Blessed7", ddd, numero,
                            "Olá, " + cliente.getNome() + ", seu agendamento foi realizado com sucesso!"
                    );
                    notificacaoPublisher.enviarSms(requestSms);
                    notificacaoPublisher.enviarWhatsapp(requestSms);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao publicar notificações do agendamento", e);
        }
    }

    @Transactional
    public ConsultaResponseDTO avaliar(Long consultaId, AvaliacaoRequestDTO dto) {
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new ConsultaNotFoundException("Consulta não encontrada."));

        if (consulta.getStatusConsulta() != StatusConsulta.CONCLUIDA) {
            throw new ConsultaNaoConcluidaException("Só é possível avaliar consultas concluídas.");
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

    @Transactional
    public ConsultaResponseDTO concluir(Long consultaId) {
        Consulta consulta = buscarConsulta(consultaId);
        validarPendente(consulta);
        consulta.setStatusConsulta(StatusConsulta.CONCLUIDA);
        return toDTO(consultaRepository.save(consulta));
    }

    @Transactional
    public ConsultaResponseDTO cancelar(Long consultaId) {
        Consulta consulta = buscarConsulta(consultaId);
        validarPendente(consulta);
        if (consulta.getCalBookingUid() != null && !consulta.getCalBookingUid().isBlank()) {
            calComService.cancelarReserva(consulta.getCalBookingUid());
        }
        consulta.setStatusConsulta(StatusConsulta.CANCELADA);
        return toDTO(consultaRepository.save(consulta));
    }

    private Consulta buscarConsulta(Long consultaId) {
        return consultaRepository.findById(consultaId)
                .orElseThrow(() -> new ConsultaNotFoundException("Consulta não encontrada."));
    }

    private void validarPendente(Consulta consulta) {
        if (consulta.getStatusConsulta() != StatusConsulta.PENDENTE) {
            throw new ConsultaStatusInvalidoException("A consulta não está pendente e não pode ser alterada.");
        }
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
                avaliacaoDTO,
                c.getCalBookingUid(),
                c.getLocalConsulta() != null ? c.getLocalConsulta().name() : null,
                c.getCliente() == null
                        ? null
                        : new ConsultaResponseDTO.ClienteResumoDTO(
                                c.getCliente().getId(),
                                c.getCliente().getNome(),
                                c.getCliente().getEmail()
                        )
        );
    }

    public ReservaDataDTO buscarDetalhesExternos(Long consultaId) {
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new ConsultaNotFoundException("Consulta não encontrada."));

        if (consulta.getCalBookingUid() == null || consulta.getCalBookingUid().isBlank()) {
            throw new ReservaExternaNotFoundException("Esta consulta não pode ser cancelada.");
        }

        return calComService.buscarDetalhesReserva(consulta.getCalBookingUid());
    }
}