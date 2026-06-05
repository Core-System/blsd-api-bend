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

import com.blessed.blsd_api_bend.model.entity.Servico;
import com.blessed.blsd_api_bend.service.ServicoService;
import com.blessed.blsd_api_bend.exception.servico.ServicoNotFoundException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ServicoController.class)
class ServicoControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private ServicoService servicoService;

    @MockitoBean
    private com.blessed.blsd_api_bend.service.AutenticacaoService autenticacaoService;

    @MockitoBean
    private com.blessed.blsd_api_bend.config.GerenciadorTokenJwt gerenciadorTokenJwt;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Nested
    @DisplayName("Testes do GET /servico")
    class ListarServicosTest {

        @Test
        @DisplayName("Deve retornar 200 e a lista com todos os serviços")
        void deveListarTodosOsServicosComSucesso() throws Exception {
            Servico s1 = new Servico();
            s1.setId(1L);
            s1.setNome("Corte de Cabelo");
            s1.setPreco(BigDecimal.valueOf(50.0));
            s1.setAvaliacao(null);
            s1.setDuracao(30);
            s1.setDescricacao("Corte degradê moderno");
            s1.setProdutos(new ArrayList<>());

            Mockito.when(servicoService.listarTodos()).thenReturn(List.of(s1));

            mockMvc.perform(get("/servico"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].nome").value("Corte de Cabelo"))
                    .andExpect(jsonPath("$[0].preco").value(50.0))
                    .andExpect(jsonPath("$[0].duracao").value(30));
        }

        @Test
        @DisplayName("Deve retornar 200 e o serviço correto ao buscar por ID existente")
        void deveBuscarPorIdComSucesso() throws Exception {
            Servico servico = new Servico();
            servico.setId(2L);
            servico.setNome("Barba Completa");
            servico.setPreco(BigDecimal.valueOf(35.0));
            servico.setAvaliacao(null);
            servico.setDuracao(25);
            servico.setDescricacao("Barba com toalha quente");
            servico.setProdutos(new ArrayList<>());

            Mockito.when(servicoService.listarPorId(2L)).thenReturn(servico);

            mockMvc.perform(get("/servico/2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(2))
                    .andExpect(jsonPath("$.nome").value("Barba Completa"))
                    .andExpect(jsonPath("$.preco").value(35.0));
        }

        @Test
        @DisplayName("Deve retornar 404 quando o ID do serviço não existir")
        void deveRetornarNotFoundAoBuscarIdInexistente() throws Exception {
            Mockito.when(servicoService.listarPorId(99L))
                    .thenThrow(new ServicoNotFoundException("Servico não encontrado"));

            mockMvc.perform(get("/servico/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Testes do POST /servico")
    class CriarServicoTest {

        @Test
        @DisplayName("Deve criar um serviço com dados válidos e retornar 201")
        void deveCriarServicoComSucesso() throws Exception {
            Servico servicoSalvo = new Servico();
            servicoSalvo.setId(1L);
            servicoSalvo.setNome("Luzes Reflexo");
            servicoSalvo.setPreco(BigDecimal.valueOf(120.0));
            servicoSalvo.setAvaliacao(null);
            servicoSalvo.setDuracao(90);
            servicoSalvo.setDescricacao("Luzes platinadas");
            servicoSalvo.setProdutos(new ArrayList<>());

            Mockito.when(servicoService.cadastrar(Mockito.any(Servico.class)))
                    .thenReturn(servicoSalvo);

            String bodyJson = """
                    {
                        "nome": "Luzes Reflexo",
                        "preco": 120.0,
                        "avaliacao": null,
                        "duracao": 90,
                        "descricacao": "Luzes platinadas",
                        "produto": []
                    }
                    """;

            mockMvc.perform(post("/servico")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Luzes Reflexo"))
                    .andExpect(jsonPath("$.preco").value(120.0))
                    .andExpect(jsonPath("$.duracao").value(90));
        }
    }

    @Nested
    @DisplayName("Testes do DELETE /servico/{id}")
    class DeletarServicoTest {

        @Test
        @DisplayName("Deve deletar o serviço e retornar status 204")
        void deveDeletarServicoComSucesso() throws Exception {
            Mockito.doNothing().when(servicoService).deletar(1L);

            mockMvc.perform(delete("/servico/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve retornar 404 ao tentar deletar um serviço inexistente")
        void deveRetornarNotFoundAoDeletarInexistente() throws Exception {
            Mockito.doThrow(new ServicoNotFoundException("Servico não encontrado"))
                    .when(servicoService).deletar(99L);

            mockMvc.perform(delete("/servico/99"))
                    .andExpect(status().isNotFound());
        }
    }
}