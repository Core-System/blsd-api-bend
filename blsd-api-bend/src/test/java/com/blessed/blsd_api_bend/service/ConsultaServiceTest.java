package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.dto.consulta.AvaliacaoRequestDTO;
import com.blessed.blsd_api_bend.dto.consulta.ConsultaResponseDTO;
import com.blessed.blsd_api_bend.model.entity.*;
import com.blessed.blsd_api_bend.model.enums.StatusConsulta;
import com.blessed.blsd_api_bend.repository.AvaliacaoRepository;
import com.blessed.blsd_api_bend.repository.ConsultaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaServiceTest {

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @InjectMocks
    private ConsultaService consultaService;

    @Nested
    @DisplayName("Testes de Avaliação de Consulta")
    class AvaliacaoTest {

        @Test
        @DisplayName("Deve avaliar consulta concluída com sucesso")
        void avaliarConsultaComSucesso() {
            // Arrange
            Long id = 1L;
            Consulta consulta = new Consulta();
            consulta.setId(id);
            consulta.setStatusConsulta(StatusConsulta.CONCLUIDA);

            AvaliacaoRequestDTO dto = new AvaliacaoRequestDTO();
            dto.setNota(5);
            dto.setDescricacao("Excelente!");

            when(consultaRepository.findById(id)).thenReturn(Optional.of(consulta));
            when(avaliacaoRepository.save(any(Avaliacao.class))).thenAnswer(i -> i.getArgument(0));
            when(consultaRepository.save(any(Consulta.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            ConsultaResponseDTO resultado = consultaService.avaliar(id, dto);

            // Assert
            assertNotNull(resultado);
            assertNotNull(resultado.getAvaliacao());
            assertEquals(5, resultado.getAvaliacao().getNota());
            verify(avaliacaoRepository, times(1)).save(any(Avaliacao.class));
        }

        @Test
        @DisplayName("Deve lançar erro ao tentar avaliar consulta pendente")
        void avaliarConsultaPendenteLancaExcecao() {
            Long id = 1L;
            Consulta consulta = new Consulta();
            consulta.setStatusConsulta(StatusConsulta.PENDENTE);

            when(consultaRepository.findById(id)).thenReturn(Optional.of(consulta));

            assertThrows(RuntimeException.class, () -> consultaService.avaliar(id, new AvaliacaoRequestDTO()));
            verify(avaliacaoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes de Listagem")
    class ListagemTest {

        @Test
        @DisplayName("Deve listar consultas de um cliente e mapear para DTO")
        void listarPorClienteSucesso() {
            Consulta c = new Consulta();
            c.setStatusConsulta(StatusConsulta.CONCLUIDA);
            c.setServicos(List.of(new Servico()));

            when(consultaRepository.findByClienteId(1L)).thenReturn(List.of(c));

            List<ConsultaResponseDTO> lista = consultaService.listarPorCliente(1L);

            assertEquals(1, lista.size());
            verify(consultaRepository, times(1)).findByClienteId(1L);
        }
    }
}