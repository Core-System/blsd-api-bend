package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.dto.produto.MovimentacaoRequestDTO;
import com.blessed.blsd_api_bend.model.entity.MovimentacaoATT;
import com.blessed.blsd_api_bend.model.entity.Produto;
import com.blessed.blsd_api_bend.repository.MovimentacaoRepositoryATT;
import com.blessed.blsd_api_bend.repository.ProdutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimentacaoServiceTest {

    @Mock
    private MovimentacaoRepositoryATT movimentacaoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private MovimentacaoService movimentacaoService;

    @Nested
    @DisplayName("Testes de Registro de Movimentação")
    class RegistrarMovimentacaoTest {

        @Test
        @DisplayName("Deve registrar entrada e incrementar estoque com sucesso")
        void registrarEntradaComSucesso() {
            // Arrange
            Produto produto = new Produto();
            produto.setId(1L);
            produto.setQuantidade(10);

            MovimentacaoRequestDTO dto = new MovimentacaoRequestDTO();
            dto.setProdutoId(1L);
            dto.setTipo(MovimentacaoATT.TipoMovimentacao.ENTRADA);
            dto.setQuantidade(5);

            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
            when(movimentacaoRepository.save(any(MovimentacaoATT.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            MovimentacaoATT resultado = movimentacaoService.registrar(dto);

            // Assert
            assertEquals(15, produto.getQuantidade());
            assertNotNull(resultado);
            verify(produtoRepository, times(1)).save(produto);
            verify(movimentacaoRepository, times(1)).save(any(MovimentacaoATT.class));
        }

        @Test
        @DisplayName("Deve registrar saída e decrementar estoque com sucesso")
        void registrarSaidaComSucesso() {
            Produto produto = new Produto();
            produto.setId(1L);
            produto.setQuantidade(20);

            MovimentacaoRequestDTO dto = new MovimentacaoRequestDTO();
            dto.setProdutoId(1L);
            dto.setTipo(MovimentacaoATT.TipoMovimentacao.SAIDA);
            dto.setQuantidade(5);

            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
            when(movimentacaoRepository.save(any(MovimentacaoATT.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            movimentacaoService.registrar(dto);

            assertEquals(15, produto.getQuantidade());
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar saída com estoque insuficiente")
        void falharAoSairComEstoqueInsuficiente() {
            Produto produto = new Produto();
            produto.setId(1L);
            produto.setQuantidade(2);

            MovimentacaoRequestDTO dto = new MovimentacaoRequestDTO();
            dto.setProdutoId(1L);
            dto.setTipo(MovimentacaoATT.TipoMovimentacao.SAIDA);
            dto.setQuantidade(10);

            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

            assertThrows(RuntimeException.class, () -> movimentacaoService.registrar(dto));
            verify(movimentacaoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes de Consulta")
    class ConsultaMovimentacaoTest {

        @Test
        @DisplayName("Deve retornar a lista das últimas 10 movimentações")
        void listarUltimasComSucesso() {
            movimentacaoService.listarUltimas();
            verify(movimentacaoRepository, times(1)).findTop10ByOrderByDataHoraDesc();
        }
    }
}