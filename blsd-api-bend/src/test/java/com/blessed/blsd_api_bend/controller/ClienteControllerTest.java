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

import com.blessed.blsd_api_bend.model.entity.Cliente;
import com.blessed.blsd_api_bend.service.ClienteService;
import com.blessed.blsd_api_bend.exception.cliente.ClienteNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private ClienteService clienteService;

    @MockitoBean
    private com.blessed.blsd_api_bend.service.AutenticacaoService autenticacaoService;

    @MockitoBean
    private com.blessed.blsd_api_bend.config.GerenciadorTokenJwt gerenciadorTokenJwt;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Nested
    @DisplayName("Testes do GET /cliente")
    class BuscarClientesTest {

        @Test
        @DisplayName("Deve listar todos os clientes e retornar 200 com array preenchido")
        void deveListarClientesComSucesso() throws Exception {
            Cliente cliente = new Cliente();
            cliente.setId(1L);
            cliente.setNome("Marcos");
            cliente.setEmail("marcos@email.com");

            Mockito.when(clienteService.listarTodos()).thenReturn(List.of(cliente));

            mockMvc.perform(get("/cliente"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].nome").value("Marcos"))
                    .andExpect(jsonPath("$[0].email").value("marcos@email.com"));
        }

        @Test
        @DisplayName("Deve retornar um cliente específico por ID com status 200")
        void deveBuscarClientePorIdComSucesso() throws Exception {
            Cliente cliente = new Cliente();
            cliente.setId(1L);
            cliente.setNome("Marcos");

            Mockito.when(clienteService.listarPorId(1L)).thenReturn(cliente);

            mockMvc.perform(get("/cliente/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Marcos"));
        }

        @Test
        @DisplayName("Deve retornar 404 quando o cliente não existir")
        void deveRetornarNotFoundAoBuscarIdInexistente() throws Exception {
            Mockito.when(clienteService.listarPorId(99L))
                    .thenThrow(new ClienteNotFoundException("Cliente não encontrado"));

            mockMvc.perform(get("/cliente/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Testes do POST /cliente")
    class CriarClienteTest {

        @Test
        @DisplayName("Deve cadastrar cliente com dados válidos e retornar 201")
        void deveCriarClienteComSucesso() throws Exception {
            Cliente clienteSalvo = new Cliente();
            clienteSalvo.setId(1L);
            clienteSalvo.setNome("Marcos");
            clienteSalvo.setEmail("marcos@email.com");

            Mockito.when(clienteService.cadastrar(Mockito.any(Cliente.class)))
                    .thenReturn(clienteSalvo);

            String bodyJson = """
                    {
                        "nome": "Marcos",
                        "email": "marcos@email.com",
                        "senha": "senhaSegura123",
                        "dataNasc": "2000-01-01",
                        "urlFoto": "http://foto.com/marcos.png",
                        "telefone": "11999999999",
                        "acesso": "CLIENTE"
                    }
                    """;

            mockMvc.perform(post("/cliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Marcos"))
                    .andExpect(jsonPath("$.email").value("marcos@email.com"));
        }
    }

    @Nested
    @DisplayName("Testes do PUT /cliente/{id}")
    class AtualizarClienteTest {

        @Test
        @DisplayName("Deve atualizar dados do cliente existente e retornar 200")
        void deveAtualizarClienteComSucesso() throws Exception {
            Cliente clienteAntigo = new Cliente();
            clienteAntigo.setId(1L);
            clienteAntigo.setNome("Marcos Antigo");

            Cliente clienteAtualizado = new Cliente();
            clienteAtualizado.setId(1L);
            clienteAtualizado.setNome("Marcos Novo");

            Mockito.when(clienteService.listarPorId(1L)).thenReturn(clienteAntigo);
            Mockito.when(clienteService.atualizar(Mockito.eq(1L), Mockito.any(Cliente.class)))
                    .thenReturn(clienteAtualizado);

            String bodyJson = """
                    {
                        "nome": "Marcos Novo",
                        "email": "marcos@email.com",
                        "senha": "novaSenha123"
                    }
                    """;

            mockMvc.perform(put("/cliente/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("Marcos Novo"));
        }
    }

    @Nested
    @DisplayName("Testes do DELETE /cliente/{id}")
    class DeletarClienteTest {

        @Test
        @DisplayName("Deve deletar cliente por ID e retornar 204")
        void deveDeletarClienteComSucesso() throws Exception {
            Mockito.doNothing().when(clienteService).deletar(1L);

            mockMvc.perform(delete("/cliente/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve retornar 404 ao tentar deletar um cliente inexistente")
        void deveRetornarNotFoundAoDeletarInexistente() throws Exception {
            Mockito.doThrow(new ClienteNotFoundException("Cliente não encontrado"))
                    .when(clienteService).deletar(99L);

            mockMvc.perform(delete("/cliente/99"))
                    .andExpect(status().isNotFound());
        }
    }
}