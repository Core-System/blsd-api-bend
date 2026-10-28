package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.config.NotificacaoPublisher;
import com.blessed.blsd_api_bend.dto.agendamento.AgendamentoRequestDTO;
import com.blessed.blsd_api_bend.dto.agendamento.ReservaDataDTO;
import com.blessed.blsd_api_bend.dto.consulta.AvaliacaoRequestDTO;
import com.blessed.blsd_api_bend.dto.consulta.ConsultaResponseDTO;
import com.blessed.blsd_api_bend.exception.agendamento.HorarioIndisponivelException;
import com.blessed.blsd_api_bend.model.entity.Avaliacao;
import com.blessed.blsd_api_bend.model.entity.Cliente;
import com.blessed.blsd_api_bend.model.entity.Consulta;
import com.blessed.blsd_api_bend.model.entity.Servico;
import com.blessed.blsd_api_bend.model.enums.StatusConsulta;
import com.blessed.blsd_api_bend.repository.AvaliacaoRepository;
import com.blessed.blsd_api_bend.repository.ClienteRepository;
import com.blessed.blsd_api_bend.repository.ConsultaRepository;
import com.blessed.blsd_api_bend.repository.ServicoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaServiceTest {

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private CalComService calComService;

    @Mock
    private NotificacaoPublisher notificacaoPublisher;

    @InjectMocks
    private ConsultaService consultaService;

    @Nested
    @DisplayName("Testes do método listarPorCliente")
    class ListarPorClienteTests {

        @Test
        @DisplayName("Deve retornar lista de ConsultaResponseDTO para cliente existente")
        void listarPorCliente_DeveRetornarListaDeDTOs_QuandoClienteExiste() {
            Long clienteId = 1L;
            Consulta consulta = new Consulta();
            consulta.setId(10L);
            consulta.setDataHoraInicio(LocalDateTime.now());
            consulta.setDataHoraFim(LocalDateTime.now().plusHours(1));
            consulta.setStatusConsulta(StatusConsulta.PENDENTE);

            when(consultaRepository.findByClienteId(clienteId)).thenReturn(List.of(consulta));

            List<ConsultaResponseDTO> resultado = consultaService.listarPorCliente(clienteId);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals(10L, resultado.get(0).id());
            verify(consultaRepository, times(1)).findByClienteId(clienteId);
        }
    }

    @Nested
    @DisplayName("Testes do método criarAgendamento")
    class CriarAgendamentoTests {

        @Test
        @DisplayName("Deve criar agendamento com sucesso quando dados e horários forem válidos")
        void criarAgendamento_DeveCriarAgendamentoComSucesso_QuandoDadosValidos() {
            String dataHoraStr = "2026-03-01T10:00:00Z";
            AgendamentoRequestDTO request = new AgendamentoRequestDTO();
            request.setNome("Maria Silva");
            request.setEmail("maria@email.com");
            request.setProcedimento("Limpeza de Pele");
            request.setDataHoraInicio(dataHoraStr);

            Cliente cliente = new Cliente();
            cliente.setId(1L);
            cliente.setEmail("maria@email.com");
            cliente.setNome("Maria Silva");

            Servico servico = new Servico();
            servico.setId(2L);
            servico.setNome("Limpeza de Pele");

            String bookingUid = "cal-uid-999";

            when(consultaRepository.findConsultasConflitantes(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of());
            when(calComService.solicitarAgendamento(request.getNome(), request.getEmail(), request.getDataHoraInicio()))
                    .thenReturn(bookingUid);
            when(clienteRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(cliente));
            when(servicoRepository.findByNome(request.getProcedimento())).thenReturn(Optional.of(servico));

            when(consultaRepository.save(any(Consulta.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ConsultaResponseDTO response = consultaService.criarAgendamento(request);

            assertNotNull(response);
            assertEquals(bookingUid, response.calBookingUid());
            assertTrue(response.servicos().contains("Limpeza de Pele"));

            verify(consultaRepository, times(1)).save(any(Consulta.class));
            verify(clienteRepository, times(1)).save(cliente);
        }

        @Test
        @DisplayName("Deve lançar HorarioIndisponivelException quando houver consulta conflitante")
        void criarAgendamento_DeveLancarExcecao_QuandoHouverConflito() {
            AgendamentoRequestDTO request = new AgendamentoRequestDTO();
            request.setDataHoraInicio("2026-03-01T10:00:00Z");

            when(consultaRepository.findConsultasConflitantes(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(new Consulta()));

            HorarioIndisponivelException exception = assertThrows(
                    HorarioIndisponivelException.class,
                    () -> consultaService.criarAgendamento(request)
            );

            assertEquals("Já existe um agendamento registrado em nosso sistema para este horário.", exception.getMessage());
            verify(calComService, never()).solicitarAgendamento(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Deve lançar RuntimeException quando o cliente não for encontrado")
        void criarAgendamento_DeveLancarExcecao_QuandoClienteNaoEncontrado() {
            AgendamentoRequestDTO request = new AgendamentoRequestDTO();
            request.setEmail("inexistente@email.com");
            request.setDataHoraInicio("2026-03-01T10:00:00Z");

            when(consultaRepository.findConsultasConflitantes(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of());
            when(calComService.solicitarAgendamento(any(), any(), any())).thenReturn("uid-123");
            when(clienteRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> consultaService.criarAgendamento(request)
            );

            assertTrue(exception.getMessage().contains("Cliente não encontrado"));
            verify(consultaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar RuntimeException quando o serviço/procedimento não for encontrado")
        void criarAgendamento_DeveLancarExcecao_QuandoServicoNaoEncontrado() {
            AgendamentoRequestDTO request = new AgendamentoRequestDTO();
            request.setEmail("maria@email.com");
            request.setProcedimento("Inexistente");
            request.setDataHoraInicio("2026-03-01T10:00:00Z");

            Cliente cliente = new Cliente();
            cliente.setEmail("maria@email.com");

            when(consultaRepository.findConsultasConflitantes(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of());
            when(calComService.solicitarAgendamento(any(), any(), any())).thenReturn("uid-123");
            when(clienteRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(cliente));
            when(servicoRepository.findByNome(request.getProcedimento())).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> consultaService.criarAgendamento(request)
            );

            assertEquals("Serviço não encontrado", exception.getMessage());
            verify(consultaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes do método avaliar")
    class AvaliarTests {

        @Test
        @DisplayName("Deve avaliar consulta concluída com sucesso")
        void avaliar_DeveSalvarAvaliacao_QuandoConsultaEstiverConcluida() {
            Long consultaId = 10L;
            AvaliacaoRequestDTO dto = new AvaliacaoRequestDTO();
            dto.setNota(5);
            dto.setDescricao("Atendimento excelente!");

            Cliente cliente = new Cliente();
            cliente.setId(1L);

            Consulta consulta = new Consulta();
            consulta.setId(consultaId);
            consulta.setStatusConsulta(StatusConsulta.CONCLUIDA);
            consulta.setCliente(cliente);

            when(consultaRepository.findById(consultaId)).thenReturn(Optional.of(consulta));

            when(avaliacaoRepository.save(any(Avaliacao.class))).thenAnswer(invocation -> {
                Avaliacao a = invocation.getArgument(0);
                a.setId(1L);
                return a;
            });

            when(consultaRepository.save(any(Consulta.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ConsultaResponseDTO response = consultaService.avaliar(consultaId, dto);

            assertNotNull(response);
            assertNotNull(response.avaliacao());

            verify(avaliacaoRepository, times(1)).save(any(Avaliacao.class));
            verify(consultaRepository, times(1)).save(consulta);
        }

        @Test
        @DisplayName("Deve lançar RuntimeException ao tentar avaliar consulta que não está concluída")
        void avaliar_DeveLancarExcecao_QuandoConsultaNaoEstiverConcluida() {
            Long consultaId = 10L;
            AvaliacaoRequestDTO dto = new AvaliacaoRequestDTO();

            Consulta consulta = new Consulta();
            consulta.setId(consultaId);
            consulta.setStatusConsulta(StatusConsulta.PENDENTE);

            when(consultaRepository.findById(consultaId)).thenReturn(Optional.of(consulta));

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> consultaService.avaliar(consultaId, dto)
            );

            assertEquals("Só é possível avaliar consultas concluídas", exception.getMessage());
            verify(avaliacaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar RuntimeException se a consulta não for encontrada")
        void avaliar_DeveLancarExcecao_QuandoConsultaNaoExiste() {
            Long consultaId = 99L;
            AvaliacaoRequestDTO dto = new AvaliacaoRequestDTO();
            when(consultaRepository.findById(consultaId)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> consultaService.avaliar(consultaId, dto)
            );

            assertEquals("Consulta não encontrada", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes do método buscarDetalhesExternos")
    class BuscarDetalhesExternosTests {

        @Test
        @DisplayName("Deve retornar detalhes da reserva do Cal.com quando UID estiver presente")
        void buscarDetalhesExternos_DeveRetornarReservaDataDTO_QuandoCalBookingUidExiste() {
            Long consultaId = 10L;
            String bookingUid = "cal-uid-123";

            Consulta consulta = new Consulta();
            consulta.setId(consultaId);
            consulta.setCalBookingUid(bookingUid);

            ReservaDataDTO reservaEsperada = mock(ReservaDataDTO.class);

            when(consultaRepository.findById(consultaId)).thenReturn(Optional.of(consulta));
            when(calComService.buscarDetalhesReserva(bookingUid)).thenReturn(reservaEsperada);

            ReservaDataDTO resultado = consultaService.buscarDetalhesExternos(consultaId);

            assertNotNull(resultado);
            assertEquals(reservaEsperada, resultado);
            verify(calComService, times(1)).buscarDetalhesReserva(bookingUid);
        }

        @Test
        @DisplayName("Deve lançar RuntimeException se a consulta não possuir vínculo com o Cal.com")
        void buscarDetalhesExternos_DeveLancarExcecao_QuandoCalBookingUidForNulo() {
            Long consultaId = 10L;
            Consulta consulta = new Consulta();
            consulta.setId(consultaId);
            consulta.setCalBookingUid(null);

            when(consultaRepository.findById(consultaId)).thenReturn(Optional.of(consulta));

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> consultaService.buscarDetalhesExternos(consultaId)
            );

            assertEquals("Esta consulta não possui vinculo com o Cal.com", exception.getMessage());
            verify(calComService, never()).buscarDetalhesReserva(anyString());
        }
    }
}