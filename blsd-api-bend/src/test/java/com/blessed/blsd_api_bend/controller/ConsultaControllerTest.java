package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.agendamento.AgendamentoRequestDTO;
import com.blessed.blsd_api_bend.dto.agendamento.ReservaDataDTO;
import com.blessed.blsd_api_bend.dto.consulta.AvaliacaoRequestDTO;
import com.blessed.blsd_api_bend.dto.consulta.ConsultaResponseDTO;
import com.blessed.blsd_api_bend.service.ConsultaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaControllerTest {

    @Mock
    private ConsultaService consultaService;

    @InjectMocks
    private ConsultaController consultaController;

    @Test
    @DisplayName("Deve agendar uma consulta com sucesso e retornar HTTP 200")
    void agendar_DeveRetornarConsultaResponseDTO_QuandoAgendamentoForSucesso() {
        AgendamentoRequestDTO request = mock(AgendamentoRequestDTO.class);
        ConsultaResponseDTO responseEsperada = mock(ConsultaResponseDTO.class);
        when(consultaService.criarAgendamento(request)).thenReturn(responseEsperada);

        ResponseEntity<ConsultaResponseDTO> response = consultaController.agendar(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseEsperada, response.getBody());
        verify(consultaService, times(1)).criarAgendamento(request);
    }

    @Test
    @DisplayName("Deve listar consultas do cliente com sucesso e retornar HTTP 200")
    void listarPorCliente_DeveRetornarListaDeConsultas_QuandoClienteIdForValido() {
        Long clienteId = 1L;
        ConsultaResponseDTO dto1 = mock(ConsultaResponseDTO.class);
        ConsultaResponseDTO dto2 = mock(ConsultaResponseDTO.class);
        List<ConsultaResponseDTO> listaEsperada = List.of(dto1, dto2);

        when(consultaService.listarPorCliente(clienteId)).thenReturn(listaEsperada);

        ResponseEntity<List<ConsultaResponseDTO>> response = consultaController.listarPorCliente(clienteId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(listaEsperada, response.getBody());
        verify(consultaService, times(1)).listarPorCliente(clienteId);
    }

    @Test
    @DisplayName("Deve avaliar consulta com sucesso e retornar HTTP 200")
    void avaliar_DeveRetornarConsultaResponseDTO_QuandoAvaliacaoForCompletada() {
        Long consultaId = 10L;
        AvaliacaoRequestDTO avaliacaoDTO = mock(AvaliacaoRequestDTO.class);
        ConsultaResponseDTO responseEsperada = mock(ConsultaResponseDTO.class);
        when(consultaService.avaliar(consultaId, avaliacaoDTO)).thenReturn(responseEsperada);

        ResponseEntity<ConsultaResponseDTO> response = consultaController.avaliar(consultaId, avaliacaoDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseEsperada, response.getBody());
        verify(consultaService, times(1)).avaliar(consultaId, avaliacaoDTO);
    }

    @Test
    @DisplayName("Deve buscar detalhes externos do Cal.com com sucesso e retornar HTTP 200")
    void buscarDetalhesCalCom_DeveRetornarReservaDataDTO_QuandoConsultaIdForValido() {
        Long consultaId = 10L;
        ReservaDataDTO reservaEsperada = mock(ReservaDataDTO.class);
        when(consultaService.buscarDetalhesExternos(consultaId)).thenReturn(reservaEsperada);

        ResponseEntity<ReservaDataDTO> response = consultaController.buscarDetalhesCalCom(consultaId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reservaEsperada, response.getBody());
        verify(consultaService, times(1)).buscarDetalhesExternos(consultaId);
    }
}