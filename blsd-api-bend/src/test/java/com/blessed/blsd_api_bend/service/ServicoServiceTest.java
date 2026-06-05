package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.exception.servico.ServicoAlreadyExistsException;
import com.blessed.blsd_api_bend.exception.servico.ServicoNotFoundException;
import com.blessed.blsd_api_bend.model.entity.Servico;
import com.blessed.blsd_api_bend.repository.ServicoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoServiceTest {

    @Mock
    private ServicoRepository servicoRepository;

    @InjectMocks
    private ServicoService servicoService;

    @Nested
    @DisplayName("Testes de Gestão de Serviços")
    class ServicoCrudTest {

        @Test
        @DisplayName("Deve cadastrar serviço com sucesso se o nome for inédito")
        void cadastrarComSucesso() {
            Servico servico = new Servico();
            servico.setNome("Corte Degradê");
            servico.setPreco(new BigDecimal("50.00"));

            when(servicoRepository.existsByNome(servico.getNome())).thenReturn(false);
            when(servicoRepository.save(any(Servico.class))).thenAnswer(inv -> inv.getArgument(0));

            Servico resultado = servicoService.cadastrar(servico);

            assertNotNull(resultado);
            assertEquals("Corte Degradê", resultado.getNome());
            verify(servicoRepository, times(1)).save(servico);
        }

        @Test
        @DisplayName("Deve lançar exceção ao cadastrar serviço com nome duplicado")
        void cadastrarNomeDuplicadoLancaExcecao() {
            Servico servico = new Servico();
            servico.setNome("Barba");

            when(servicoRepository.existsByNome("Barba")).thenReturn(true);

            assertThrows(ServicoAlreadyExistsException.class, () -> {
                servicoService.cadastrar(servico);
            });
            verify(servicoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar serviço ao buscar por ID existente")
        void buscarPorIdComSucesso() {
            Servico servico = new Servico();
            servico.setId(1L);
            servico.setNome("Massagem");

            when(servicoRepository.findById(1L)).thenReturn(Optional.of(servico));

            Servico resultado = servicoService.listarPorId(1L);

            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar serviço por ID inexistente")
        void buscarPorIdInexistenteLancaExcecao() {
            when(servicoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ServicoNotFoundException.class, () -> {
                servicoService.listarPorId(99L);
            });
        }
    }
}