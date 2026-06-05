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

import com.blessed.blsd_api_bend.service.CalComService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalComController.class)
class CalComControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private CalComService calComService;

    @MockitoBean
    private com.blessed.blsd_api_bend.service.AutenticacaoService autenticacaoService;

    @MockitoBean
    private com.blessed.blsd_api_bend.config.GerenciadorTokenJwt gerenciadorTokenJwt;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Nested
    @DisplayName("Testes do POST /api/calendario/calcom/agendar")
    class AgendarTest {

        @Test
        @DisplayName("Deve realizar um agendamento com sucesso e retornar 200")
        void deveAgendarComSucesso() throws Exception {
            Mockito.when(calComService.criarAgendamento(
                    Mockito.eq("Flavio"),
                    Mockito.eq("flavio@email.com"),
                    Mockito.eq("2026-06-05T10:00:00Z"))
            ).thenReturn("Sucesso: 201 CREATED");

            String bodyJson = """
                    {
                        "nome": "Flavio",
                        "email": "flavio@email.com",
                        "dataHoraInicio": "2026-06-05T10:00:00Z"
                    }
                    """;

            mockMvc.perform(post("/api/calendario/calcom/agendar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyJson))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Sucesso: 201 CREATED"));
        }
    }
}