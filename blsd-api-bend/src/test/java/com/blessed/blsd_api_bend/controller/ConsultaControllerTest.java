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

import com.blessed.blsd_api_bend.dto.consulta.AvaliacaoDTO;
import com.blessed.blsd_api_bend.dto.consulta.ConsultaResponseDTO;
import com.blessed.blsd_api_bend.dto.consulta.AvaliacaoRequestDTO;
import com.blessed.blsd_api_bend.service.ConsultaService;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultaController.class)
class ConsultaControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private ConsultaService consultaService;

    @MockitoBean
    private com.blessed.blsd_api_bend.service.AutenticacaoService autenticacaoService;

    @MockitoBean
    private com.blessed.blsd_api_bend.config.GerenciadorTokenJwt gerenciadorTokenJwt;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Nested
    @DisplayName("Testes do GET /consulta/cliente/{clienteId}")
    class ListarPorClienteTest {

        @Test
        @DisplayName("Deve retornar status 200 e a lista de consultas do cliente")
        void deveListarConsultasPorClienteComSucesso() throws Exception {
            AvaliacaoDTO avaliacao = new AvaliacaoDTO(1L, 5, "Excelente atendimento");
            ConsultaResponseDTO consultaDTO = new ConsultaResponseDTO(
                    1L,
                    LocalDateTime.of(2026, 6, 15, 14, 0),
                    LocalDateTime.of(2026, 6, 15, 15, 0),
                    "CONCLUIDA",
                    List.of("Corte de Cabelo"),
                    avaliacao
            );

            Mockito.when(consultaService.listarPorCliente(1L))
                    .thenReturn(List.of(consultaDTO));

            mockMvc.perform(get("/consulta/cliente/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].servicos[0]").value("Corte de Cabelo"))
                    .andExpect(jsonPath("$[0].avaliacao.nota").value(5));
        }
    }

    @Nested
    @DisplayName("Testes do POST /consulta/{consultaId}/avaliar")
    class AvaliarConsultaTest {

        @Test
        @DisplayName("Deve registrar avaliação de uma consulta e retornar 200")
        void deveAvaliarConsultaComSucesso() throws Exception {
            AvaliacaoDTO avaliacaoDTO = new AvaliacaoDTO(2L, 4, "Muito bom");
            ConsultaResponseDTO respostaDTO = new ConsultaResponseDTO(
                    10L,
                    LocalDateTime.of(2026, 6, 10, 10, 0),
                    LocalDateTime.of(2026, 6, 10, 11, 0),
                    "CONCLUIDA",
                    List.of("Luzes"),
                    avaliacaoDTO
            );

            Mockito.when(consultaService.avaliar(Mockito.eq(10L), Mockito.any(AvaliacaoRequestDTO.class)))
                    .thenReturn(respostaDTO);

            String bodyJson = """
                    {
                        "nota": 4,
                        "descricacao": "Muito bom"
                    }
                    """;

            mockMvc.perform(post("/consulta/10/avaliar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(10))
                    .andExpect(jsonPath("$.avaliacao.nota").value(4))
                    .andExpect(jsonPath("$.avaliacao.descricacao").value("Muito bom"));
        }
    }
}