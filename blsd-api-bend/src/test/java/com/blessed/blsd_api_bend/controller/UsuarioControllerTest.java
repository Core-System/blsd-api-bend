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

import com.blessed.blsd_api_bend.dto.usuario.LoginRequestDTO;
import com.blessed.blsd_api_bend.dto.usuario.UsuarioTokenDTO;
import com.blessed.blsd_api_bend.model.entity.Cliente;
import com.blessed.blsd_api_bend.model.entity.Funcionario;
import com.blessed.blsd_api_bend.service.ClienteService;
import com.blessed.blsd_api_bend.service.FuncionarioService;
import com.blessed.blsd_api_bend.service.UsuarioService;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private ClienteService clienteService;

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
    @DisplayName("Testes do POST /usuarios/login")
    class LoginTest {

        @Test
        @DisplayName("Deve realizar login com sucesso e retornar token com status 200")
        void deveLogarComSucesso() throws Exception {
            UsuarioTokenDTO tokenMock = new UsuarioTokenDTO(1L, "Admin", "login@email.com", "token-fake", null);

            Mockito.when(usuarioService.autenticar(Mockito.any(LoginRequestDTO.class)))
                    .thenReturn(tokenMock);

            String bodyJson = """
                    {
                        "email": "login@email.com",
                        "senha": "123"
                    }
                    """;

            mockMvc.perform(post("/usuarios/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Admin"))
                    .andExpect(jsonPath("$.email").value("login@email.com"))
                    .andExpect(jsonPath("$.token").value("token-fake"));
        }
    }

    @Nested
    @DisplayName("Testes do GET /usuarios/clientes")
    class ListarClientesTest {

        @Test
        @DisplayName("Deve retornar status 200 e a lista de clientes cadastrados")
        void deveListarClientesComSucesso() throws Exception {
            Cliente cliente = new Cliente();
            cliente.setId(1L);
            cliente.setNome("Carlos");

            Mockito.when(clienteService.listarTodos()).thenReturn(List.of(cliente));

            mockMvc.perform(get("/usuarios/clientes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].nome").value("Carlos"));
        }

        @Test
        @DisplayName("Deve retornar status 204 No Content quando não houver clientes")
        void deveRetornarNoContentQuandoListaClientesVazia() throws Exception {
            Mockito.when(clienteService.listarTodos()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/usuarios/clientes"))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("Testes do GET /usuarios/funcionarios")
    class ListarFuncionariosTest {

        @Test
        @DisplayName("Deve retornar status 200 e a lista de funcionários cadastrados")
        void deveListarFuncionariosComSucesso() throws Exception {
            Funcionario funcionario = new Funcionario();
            funcionario.setId(2L);
            funcionario.setNome("Ana Barbeira");

            Mockito.when(funcionarioService.listarTodos()).thenReturn(List.of(funcionario));

            mockMvc.perform(get("/usuarios/funcionarios"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(2))
                    .andExpect(jsonPath("$[0].nome").value("Ana Barbeira"));
        }

        @Test
        @DisplayName("Deve retornar status 204 No Content quando não houver funcionários")
        void deveRetornarNoContentQuandoListaFuncionariosVazia() throws Exception {
            Mockito.when(funcionarioService.listarTodos()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/usuarios/funcionarios"))
                    .andExpect(status().isNoContent());
        }
    }
}