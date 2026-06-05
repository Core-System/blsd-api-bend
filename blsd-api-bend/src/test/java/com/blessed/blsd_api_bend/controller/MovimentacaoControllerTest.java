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

import com.blessed.blsd_api_bend.dto.produto.MovimentacaoRequestDTO;
import com.blessed.blsd_api_bend.model.entity.MovimentacaoATT;
import com.blessed.blsd_api_bend.model.entity.Produto;
import com.blessed.blsd_api_bend.service.MovimentacaoService;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovimentacaoController.class)
class MovimentacaoControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private MovimentacaoService movimentacaoService;

    @MockitoBean
    private com.blessed.blsd_api_bend.service.AutenticacaoService autenticacaoService;

    @MockitoBean
    private com.blessed.blsd_api_bend.config.GerenciadorTokenJwt gerenciadorTokenJwt;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Nested
    @DisplayName("Testes do GET /movimentacao")
    class ListarUltimasMovimentacoesTest {

        @Test
        @DisplayName("Deve retornar status 200 e a lista das últimas movimentações mapeadas")
        void deveListarUltimasMovimentacoesComSucesso() throws Exception {
            Produto produto = new Produto();
            produto.setId(10L);
            produto.setNome("Shampoo Forte");

            MovimentacaoATT mov = new MovimentacaoATT();
            mov.setId(1L);
            mov.setProduto(produto);
            mov.setTipo(MovimentacaoATT.TipoMovimentacao.ENTRADA);
            mov.setQuantidade(5);
            mov.setDataHora(LocalDateTime.now());
            mov.setObservacao("Reposição de estoque");

            Mockito.when(movimentacaoService.listarUltimas()).thenReturn(List.of(mov));

            mockMvc.perform(get("/movimentacao"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].quantidade").value(5))
                    .andExpect(jsonPath("$[0].observacao").value("Reposição de estoque"));
        }
    }

    @Nested
    @DisplayName("Testes do POST /movimentacao")
    class RegistrarMovimentacaoTest {

        @Test
        @DisplayName("Deve registrar uma movimentação com sucesso e retornar status 201")
        void deveRegistrarMovimentacaoComSucesso() throws Exception {
            Produto produto = new Produto();
            produto.setId(20L);
            produto.setNome("Pomada Modeladora");

            MovimentacaoATT movSalva = new MovimentacaoATT();
            movSalva.setId(5L);
            movSalva.setProduto(produto);
            movSalva.setTipo(MovimentacaoATT.TipoMovimentacao.SAIDA);
            movSalva.setQuantidade(2);
            movSalva.setDataHora(LocalDateTime.now());
            movSalva.setObservacao("Venda balcão");

            Mockito.when(movimentacaoService.registrar(Mockito.any(MovimentacaoRequestDTO.class)))
                    .thenReturn(movSalva);

            String bodyJson = """
                    {
                        "produtoId": 20,
                        "tipo": "SAIDA",
                        "quantidade": 2,
                        "observacao": "Venda balcão"
                    }
                    """;

            mockMvc.perform(post("/movimentacao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(5))
                    .andExpect(jsonPath("$.tipo").value("SAIDA"))
                    .andExpect(jsonPath("$.quantidade").value(2));
        }
    }
}