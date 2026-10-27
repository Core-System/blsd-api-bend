package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.config.clientes.CalComCliente;
import com.blessed.blsd_api_bend.dto.agendamento.*;
import com.blessed.blsd_api_bend.exception.agendamento.HorarioIndisponivelException;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalComServiceTest {

    @Mock
    private CalComCliente calComClient;

    @InjectMocks
    private CalComService calComService;

    private final String API_KEY = "test-api-key";
    private final String API_VERSION = "2026-02-25";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(calComService, "apiKey", API_KEY);
        ReflectionTestUtils.setField(calComService, "apiVersion", API_VERSION);
    }

    private FeignException createFeignException(int status, String message) {
        Request request = Request.create(Request.HttpMethod.POST, "/url", Collections.emptyMap(), null, StandardCharsets.UTF_8, null);
        Response response = Response.builder()
                .status(status)
                .reason(message)
                .request(request)
                .body(message, StandardCharsets.UTF_8)
                .build();
        return FeignException.errorStatus("methodKey", response);
    }

    @Nested
    @DisplayName("Testes do método solicitarAgendamento")
    class SolicitarAgendamentoTests {

        @Test
        @DisplayName("Deve retornar UID do agendamento quando a chamada ao CalCom for bem-sucedida")
        void solicitarAgendamento_DeveRetornarUid_QuandoCalComResponderComSucesso() {
            String nome = "Maria Silva";
            String email = "maria@email.com";
            String dataInicio = "2026-03-01T10:00:00Z";
            String expectedUid = "uid-123456";

            CalComResponseDTO.CalBookingDataDTO bookingData = new CalComResponseDTO.CalBookingDataDTO(
                    1L, expectedUid, "ACCEPTED", OffsetDateTime.now(), OffsetDateTime.now().plusHours(1), "Consulta", "Descrição"
            );
            CalComResponseDTO responseDTO = new CalComResponseDTO("success", bookingData);

            when(calComClient.criarAgendamento(eq("Bearer " + API_KEY), eq(API_VERSION), any(CalComRequisicaoDTO.class)))
                    .thenReturn(ResponseEntity.ok(responseDTO));

            String uidRetornado = calComService.solicitarAgendamento(nome, email, dataInicio);

            assertEquals(expectedUid, uidRetornado);

            ArgumentCaptor<CalComRequisicaoDTO> captor = ArgumentCaptor.forClass(CalComRequisicaoDTO.class);
            verify(calComClient, times(1)).criarAgendamento(eq("Bearer " + API_KEY), eq(API_VERSION), captor.capture());

            CalComRequisicaoDTO dtoEnviado = captor.getValue();
            assertEquals(5805309, dtoEnviado.getEventTypeId());
            assertEquals(dataInicio, dtoEnviado.getStart());
            assertEquals(nome, dtoEnviado.getAttendee().getName());
            assertEquals(email, dtoEnviado.getAttendee().getEmail());
        }

        @Test
        @DisplayName("Deve lançar RuntimeException quando o corpo da resposta do CalCom for nulo")
        void solicitarAgendamento_DeveLancarRuntimeException_QuandoBodyForNulo() {
            when(calComClient.criarAgendamento(anyString(), anyString(), any(CalComRequisicaoDTO.class)))
                    .thenReturn(ResponseEntity.ok(null));

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> calComService.solicitarAgendamento("Maria", "maria@email.com", "2026-03-01T10:00:00Z")
            );

            assertEquals("A resposta da API do Cal.com não retornou o UID da reserva.", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar HorarioIndisponivelException quando o CalCom retornar FeignException.Conflict (409)")
        void solicitarAgendamento_DeveLancarHorarioIndisponivelException_QuandoConflito() {
            FeignException.Conflict conflictException = (FeignException.Conflict) createFeignException(409, "Horário indisponível");
            when(calComClient.criarAgendamento(anyString(), anyString(), any(CalComRequisicaoDTO.class)))
                    .thenThrow(conflictException);

            HorarioIndisponivelException exception = assertThrows(
                    HorarioIndisponivelException.class,
                    () -> calComService.solicitarAgendamento("Maria", "maria@email.com", "2026-03-01T10:00:00Z")
            );

            assertEquals("O horário selecionado já está reservado.", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException (400) quando o CalCom retornar FeignException.BadRequest (400)")
        void solicitarAgendamento_DeveLancarResponseStatusException400_QuandoBadRequest() {
            FeignException.BadRequest badRequestException = (FeignException.BadRequest) createFeignException(400, "Bad Request");
            when(calComClient.criarAgendamento(anyString(), anyString(), any(CalComRequisicaoDTO.class)))
                    .thenThrow(badRequestException);

            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> calComService.solicitarAgendamento("Maria", "maria@email.com", "2026-03-01T10:00:00Z")
            );

            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertTrue(exception.getReason().contains("Os dados enviados para o agendamento são inválidos."));
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException (503) quando ocorrer FeignException genérica")
        void solicitarAgendamento_DeveLancarResponseStatusException503_QuandoErroGenericoFeign() {
            FeignException genericFeignException = createFeignException(500, "Internal Server Error");
            when(calComClient.criarAgendamento(anyString(), anyString(), any(CalComRequisicaoDTO.class)))
                    .thenThrow(genericFeignException);

            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> calComService.solicitarAgendamento("Maria", "maria@email.com", "2026-03-01T10:00:00Z")
            );

            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatusCode());
            assertTrue(exception.getReason().contains("Serviço de agendamento externo indisponível no momento."));
        }
    }

    @Nested
    @DisplayName("Testes do método buscarDetalhesReserva")
    class BuscarDetalhesReservaTests {

        @Test
        @DisplayName("Deve retornar os detalhes da reserva quando UID for válido e encontrado no CalCom")
        void buscarDetalhesReserva_DeveRetornarReservaDataDTO_QuandoUidValido() {
            String uid = "uid-123456";
            ReservaDataDTO reservaEsperada = new ReservaDataDTO(
                    1L, uid, "Consulta Studio", "Atendimento",
                    "2026-03-01T10:00:00Z", "2026-03-01T11:00:00Z",
                    "ACCEPTED", null, List.of()
            );

            CalComReservaDetalheDTO responseDTO = mock(CalComReservaDetalheDTO.class);
            when(responseDTO.data()).thenReturn(reservaEsperada);

            when(calComClient.buscarReservaPorUid(eq("Bearer " + API_KEY), eq(API_VERSION), eq(uid)))
                    .thenReturn(ResponseEntity.ok(responseDTO));

            ReservaDataDTO resultado = calComService.buscarDetalhesReserva(uid);

            assertNotNull(resultado);
            assertEquals(reservaEsperada, resultado);
            assertEquals(uid, resultado.uid());
            verify(calComClient, times(1)).buscarReservaPorUid(eq("Bearer " + API_KEY), eq(API_VERSION), eq(uid));
        }

        @Test
        @DisplayName("Deve lançar RuntimeException quando o corpo da resposta ou data for nulo")
        void buscarDetalhesReserva_DeveLancarRuntimeException_QuandoBodyForNulo() {
            String uid = "uid-123456";
            when(calComClient.buscarReservaPorUid(anyString(), anyString(), anyString()))
                    .thenReturn(ResponseEntity.ok(null));

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> calComService.buscarDetalhesReserva(uid)
            );

            assertEquals("Reserva não encontrada no Cal.com", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException (503) quando a chamada de busca falhar com FeignException")
        void buscarDetalhesReserva_DeveLancarResponseStatusException503_QuandoFeignException() {
            String uid = "uid-123456";
            FeignException feignException = createFeignException(404, "Not Found");
            when(calComClient.buscarReservaPorUid(anyString(), anyString(), anyString()))
                    .thenThrow(feignException);

            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> calComService.buscarDetalhesReserva(uid)
            );

            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatusCode());
            assertTrue(exception.getReason().contains("Não foi possível consultar os detalhes do agendamento no Cal.com."));
        }
    }
}