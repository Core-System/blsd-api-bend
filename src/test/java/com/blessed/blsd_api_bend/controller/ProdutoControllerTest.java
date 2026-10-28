package com.blessed.blsd_api_bend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.blessed.blsd_api_bend.model.entity.Produto;
import com.blessed.blsd_api_bend.service.ProdutoService;
import com.blessed.blsd_api_bend.exception.produto.ProdutoNotFoundException;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProdutoController.class)
class ProdutoControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private ProdutoService produtoService;

    @MockitoBean
    private com.blessed.blsd_api_bend.service.AutenticacaoService autenticacaoService;

    @MockitoBean
    private com.blessed.blsd_api_bend.config.GerenciadorTokenJwt gerenciadorTokenJwt;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Nested
    @DisplayName("Testes do GET /produto")
    class BuscarProdutos {
        @Test
        @DisplayName("Deve listar todos os produtos e retornar 200 com array preenchido")
        void deveListarProdutosComSucesso() throws Exception {
            Produto p1 = new Produto();
            p1.setId(1L);
            p1.setNome("Shampoo");
            p1.setPreco(BigDecimal.valueOf(25.0));
            p1.setQuantidade(10);

            Mockito.when(produtoService.listarTodos()).thenReturn(List.of(p1));

            mockMvc.perform(get("/produto"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].nome").value("Shampoo"))
                    .andExpect(jsonPath("$[0].preco").value(25.0))
                    .andExpect(jsonPath("$[0].quantidade").value(10));
        }

        @Test
        @DisplayName("Deve retornar um produto específico por ID com 200")
        void deveBuscarProdutoPorIdComSucesso() throws Exception {
            Produto produto = new Produto();
            produto.setId(1L);
            produto.setNome("Condicionador");
            produto.setPreco(BigDecimal.valueOf(30.0));
            produto.setQuantidade(5);

            Mockito.when(produtoService.listarPorId(1L)).thenReturn(produto);

            mockMvc.perform(get("/produto/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Condicionador"))
                    .andExpect(jsonPath("$.preco").value(30.0))
                    .andExpect(jsonPath("$.quantidade").value(5));
        }

        @Test
        @DisplayName("Deve retornar 404 quando o produto não existir por ID")
        void deveRetornarNotFoundAoBuscarIdInexistente() throws Exception {
            Mockito.when(produtoService.listarPorId(99L))
                    .thenThrow(new ProdutoNotFoundException("Produto não encontrado"));

            mockMvc.perform(get("/produto/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Testes do POST /produto")
    class CriarProdutoTest {

        @Test
        @DisplayName("Deve criar produto com dados válidos e retornar 201")
        void deveCriarProdutoComSucesso() throws Exception {
            Produto produtoSalvo = new Produto();
            produtoSalvo.setId(1L);
            produtoSalvo.setNome("Pomada Modeladora");
            produtoSalvo.setPreco(BigDecimal.valueOf(45.0));
            produtoSalvo.setQuantidade(15);

            Mockito.when(produtoService.cadastrar(Mockito.any(Produto.class)))
                    .thenReturn(produtoSalvo);

            String bodyJson = """
                    {
                        "nome": "Pomada Modeladora",
                        "preco": 45.0,
                        "quantidade": 15
                    }
                    """;

            mockMvc.perform(post("/produto")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Pomada Modeladora"))
                    .andExpect(jsonPath("$.preco").value(45.0))
                    .andExpect(jsonPath("$.quantidade").value(15));
        }
    }

    @Nested
    @DisplayName("Testes do PUT /produto/{id}")
    class AtualizarProdutoTest {

        @Test
        @DisplayName("Deve atualizar produto existente com sucesso e retornar 200")
        void deveAtualizarProdutoComSucesso() throws Exception {
            Produto produtoAntigo = new Produto();
            produtoAntigo.setId(1L);
            produtoAntigo.setNome("Óleo Antigo");
            produtoAntigo.setPreco(BigDecimal.valueOf(20.0));
            produtoAntigo.setQuantidade(2);

            Produto produtoAtualizado = new Produto();
            produtoAtualizado.setId(1L);
            produtoAtualizado.setNome("Óleo Premium");
            produtoAtualizado.setPreco(BigDecimal.valueOf(35.0));
            produtoAtualizado.setQuantidade(8);

            Mockito.when(produtoService.listarPorId(1L)).thenReturn(produtoAntigo);
            Mockito.when(produtoService.atualizar(Mockito.eq(1L), Mockito.any(Produto.class)))
                    .thenReturn(produtoAtualizado);

            String bodyJson = """
                    {
                        "nome": "Óleo Premium",
                        "preco": 35.0,
                        "quantidade": 8
                    }
                    """;

            mockMvc.perform(put("/produto/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("Óleo Premium"))
                    .andExpect(jsonPath("$.preco").value(35.0))
                    .andExpect(jsonPath("$.quantidade").value(8));
        }
    }

    @Nested
    @DisplayName("Testes do DELETE /produto/{id}")
    class DeletarProdutoTest {

        @Test
        @DisplayName("Deve deletar produto por ID e retornar 204")
        void deveDeletarProdutoComSucesso() throws Exception {
            Mockito.doNothing().when(produtoService).deletar(1L);

            mockMvc.perform(delete("/produto/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve retornar 404 ao tentar deletar um produto que não existe")
        void deveRetornarNotFoundAoDeletarInexistente() throws Exception {
            Mockito.doThrow(new ProdutoNotFoundException("Produto não encontrado"))
                    .when(produtoService).deletar(99L);

            mockMvc.perform(delete("/produto/99"))
                    .andExpect(status().isNotFound());
        }
    }
}