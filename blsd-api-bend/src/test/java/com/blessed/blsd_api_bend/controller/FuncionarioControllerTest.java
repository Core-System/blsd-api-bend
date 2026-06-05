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

import com.blessed.blsd_api_bend.model.entity.Funcionario;
import com.blessed.blsd_api_bend.service.FuncionarioService;
import com.blessed.blsd_api_bend.exception.funcionario.FuncionarioNotFoundException;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FuncionarioController.class)
class FuncionarioControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private FuncionarioService funcionarioService;

    @MockitoBean
    private com.blessed.blsd_api_bend.service.AutenticacaoService autenticacaoService;

    @MockitoBean
    private com.blessed.blsd_api_bend.config.GerenciadorTokenJwt gerenciadorTokenJwt;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Nested
    @DisplayName("Testes do GET /funcionario")
    class BuscarFuncionariosTest {

        @Test
        @DisplayName("Deve listar todos os funcionários com status 200")
        void deveListarFuncionariosComSucesso() throws Exception {
            Funcionario f = new Funcionario();
            f.setId(1L);
            f.setNome("Julia");
            f.setEmail("julia@empresa.com");

            Mockito.when(funcionarioService.listarTodos()).thenReturn(List.of(f));

            mockMvc.perform(get("/funcionario"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].nome").value("Julia"));
        }

        @Test
        @DisplayName("Deve buscar funcionário por ID com status 200")
        void deveBuscarPorIdComSucesso() throws Exception {
            Funcionario f = new Funcionario();
            f.setId(2L);
            f.setNome("Marcos");

            Mockito.when(funcionarioService.listarPorId(2L)).thenReturn(f);

            mockMvc.perform(get("/funcionario/2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(2))
                    .andExpect(jsonPath("$.nome").value("Marcos"));
        }

        @Test
        @DisplayName("Deve retornar status 404 se o funcionário não existir")
        void deveRetornarNotFoundAoBuscarIdInexistente() throws Exception {
            Mockito.when(funcionarioService.listarPorId(99L))
                    .thenThrow(new FuncionarioNotFoundException("Funcionario não encontrado"));

            mockMvc.perform(get("/funcionario/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Testes do POST /funcionario")
    class CriarFuncionarioTest {

        @Test
        @DisplayName("Deve cadastrar um funcionário válido e retornar status 201")
        void deveCriarFuncionarioComSucesso() throws Exception {
            Funcionario funcSalvo = new Funcionario();
            funcSalvo.setId(2L);
            funcSalvo.setNome("Julia");
            funcSalvo.setEmail("julia@empresa.com");

            Mockito.when(funcionarioService.cadastrar(Mockito.any(Funcionario.class)))
                    .thenReturn(funcSalvo);

            String bodyJson = """
                    {
                        "nome": "Julia",
                        "email": "julia@empresa.com",
                        "senha": "password123",
                        "cpf": "529.982.247-25",
                        "urlFoto": "http://foto.com/julia.png",
                        "acesso": {}
                    }
                    """;

            mockMvc.perform(post("/funcionario")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(2))
                    .andExpect(jsonPath("$.nome").value("Julia"));
        }
    }

    @Nested
    @DisplayName("Testes do PUT /funcionario/{id}")
    class AtualizarFuncionarioTest {

        @Test
        @DisplayName("Deve atualizar dados do funcionário com status 200")
        void deveAtualizarFuncionarioComSucesso() throws Exception {
            Funcionario antigo = new Funcionario();
            antigo.setId(1L);
            antigo.setNome("Julia Antiga");

            Funcionario atualizado = new Funcionario();
            atualizado.setId(1L);
            atualizado.setNome("Julia Atualizada");

            Mockito.when(funcionarioService.listarPorId(1L)).thenReturn(antigo);
            Mockito.when(funcionarioService.atualizar(Mockito.eq(1L), Mockito.any(Funcionario.class)))
                    .thenReturn(atualizado);

            String bodyJson = """
                    {
                        "nome": "Julia Atualizada",
                        "email": "julia@empresa.com",
                        "senha": "novaSenha123",
                        "cpf": "529.982.247-25",
                        "urlFoto": "http://foto.com/julia.png",
                        "acesso": {}
                    }
                    """;

            mockMvc.perform(put("/funcionario/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("Julia Atualizada"));
        }
    }

    @Nested
    @DisplayName("Testes do DELETE /funcionario/{id}")
    class DeletarFuncionarioTest {

        @Test
        @DisplayName("Deve deletar funcionário por ID e retornar status 204")
        void deveDeletarFuncionarioComSucesso() throws Exception {
            Mockito.doNothing().when(funcionarioService).deletar(1L);

            mockMvc.perform(delete("/funcionario/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve retornar status 404 ao tentar deletar id inexistente")
        void deveRetornarNotFoundAoDeletarInexistente() throws Exception {
            Mockito.doThrow(new FuncionarioNotFoundException("Funcionario não encontrado"))
                    .when(funcionarioService).deletar(99L);

            mockMvc.perform(delete("/funcionario/99"))
                    .andExpect(status().isNotFound());
        }
    }
}