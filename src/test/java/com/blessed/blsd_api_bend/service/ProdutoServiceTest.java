package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.exception.produto.ProdutoAlreadyExistsException;
import com.blessed.blsd_api_bend.exception.produto.ProdutoNotFoundException;
import com.blessed.blsd_api_bend.model.entity.Produto;
import com.blessed.blsd_api_bend.repository.ProdutoRepository;
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
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    @Nested
    @DisplayName("Testes de Gestão de Produtos")
    class ProdutoCrudTest {

        @Test
        @DisplayName("Deve cadastrar produto com sucesso se o nome for inédito")
        void cadastrarComSucesso() {
            Produto produto = new Produto();
            produto.setNome("Gel Fixador");
            produto.setPreco(new BigDecimal("25.00"));
            produto.setQuantidade(100);

            when(produtoRepository.existsByNome(produto.getNome())).thenReturn(false);
            when(produtoRepository.save(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));

            Produto resultado = produtoService.cadastrar(produto);

            assertNotNull(resultado);
            assertEquals("Gel Fixador", resultado.getNome());
            verify(produtoRepository, times(1)).save(produto);
        }

        @Test
        @DisplayName("Deve lançar exceção ao cadastrar produto com nome já existente")
        void cadastrarNomeDuplicadoLancaExcecao() {
            Produto produto = new Produto();
            produto.setNome("Pomada Capilar");

            when(produtoRepository.existsByNome("Pomada Capilar")).thenReturn(true);

            assertThrows(ProdutoAlreadyExistsException.class, () -> {
                produtoService.cadastrar(produto);
            });
            verify(produtoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar produto ao buscar por ID existente")
        void buscarPorIdComSucesso() {
            Produto produto = new Produto();
            produto.setId(1L);
            produto.setNome("Shampoo");

            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

            Produto resultado = produtoService.listarPorId(1L);

            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar produto por ID inexistente")
        void buscarPorIdInexistenteLancaExcecao() {
            when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ProdutoNotFoundException.class, () -> {
                produtoService.listarPorId(99L);
            });
        }
    }
}