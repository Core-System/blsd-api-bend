package com.blessed.blsd_api_bend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import com.blessed.blsd_api_bend.config.GerenciadorTokenJwt;
import com.blessed.blsd_api_bend.dto.funcionario.FuncionarioAtualizarRequestDTO;
import com.blessed.blsd_api_bend.dto.funcionario.FuncionarioRequestDTO;
import com.blessed.blsd_api_bend.dto.funcionario.FuncionarioResponseDTO;
import com.blessed.blsd_api_bend.exception.funcionario.FuncionarioNotFoundException;
import com.blessed.blsd_api_bend.model.entity.Funcionario;
import com.blessed.blsd_api_bend.repository.AcessoRepository;
import com.blessed.blsd_api_bend.service.AutenticacaoService;
import com.blessed.blsd_api_bend.service.FuncionarioService;

@WebMvcTest(FuncionarioController.class)
class FuncionarioControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private FuncionarioService funcionarioService;

    // CORREÇÃO: o construtor da controller exige AcessoRepository.
    // Sem este mock o contexto do @WebMvcTest não sobe.
    @MockitoBean
    private AcessoRepository acessoRepository;

    @MockitoBean
    private AutenticacaoService autenticacaoService;

    @MockitoBean
    private GerenciadorTokenJwt gerenciadorTokenJwt;

    private static final String BODY_FUNCIONARIO = """
            {
                "nome": "Julia",
                "email": "julia@empresa.com",
                "senha": "password123",
                "cpf": "529.982.247-25",
                "urlFoto": "http://foto.com/julia.png",
                "acesso": {}
            }
            """;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    private Funcionario funcionario(Long id, String nome, String email) {
        Funcionario f = new Funcionario();
        f.setId(id);
        f.setNome(nome);
        f.setEmail(email);
        return f;
    }

    @Nested
    @DisplayName("Testes do GET /funcionario")
    class BuscarFuncionariosTest {

        @Test
        @DisplayName("Deve listar todos os funcionários com status 200")
        void deveListarFuncionariosComSucesso() throws Exception {
            FuncionarioResponseDTO dto = new FuncionarioResponseDTO(1L, "Julia", "julia@empresa.com", null, null, null);
            Pageable pageable = PageRequest.of(0, 1);

            when(funcionarioService.listarTodos(any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(dto), pageable, 1));

            mockMvc.perform(get("/funcionario")
                            .param("page", "0")
                            .param("size", "1")
                            .param("sort", "nome,asc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].id").value(1))
                    .andExpect(jsonPath("$.content[0].nome").value("Julia"));

            // Garante que page/size/sort da query string chegaram ao service
            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(funcionarioService).listarTodos(captor.capture());
            Pageable recebido = captor.getValue();
            org.junit.jupiter.api.Assertions.assertEquals(0, recebido.getPageNumber());
            org.junit.jupiter.api.Assertions.assertEquals(1, recebido.getPageSize());
            org.junit.jupiter.api.Assertions.assertEquals("ASC",
                    recebido.getSort().getOrderFor("nome").getDirection().name());
        }

        @Test
        @DisplayName("Deve usar paginação padrão (size 10, sort id ASC) quando nada é informado")
        void deveUsarPaginacaoPadrao() throws Exception {
            when(funcionarioService.listarTodos(any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of()));

            mockMvc.perform(get("/funcionario"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(0));

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(funcionarioService).listarTodos(captor.capture());
            Pageable recebido = captor.getValue();
            org.junit.jupiter.api.Assertions.assertEquals(0, recebido.getPageNumber());
            org.junit.jupiter.api.Assertions.assertEquals(10, recebido.getPageSize());
            org.junit.jupiter.api.Assertions.assertEquals("ASC",
                    recebido.getSort().getOrderFor("id").getDirection().name());
        }

        @Test
        @DisplayName("Deve buscar funcionário por ID com status 200")
        void deveBuscarPorIdComSucesso() throws Exception {
            when(funcionarioService.listarPorId(2L))
                    .thenReturn(funcionario(2L, "Marcos", "marcos@empresa.com"));

            mockMvc.perform(get("/funcionario/2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(2))
                    .andExpect(jsonPath("$.nome").value("Marcos"));
        }

        @Test
        @DisplayName("Deve retornar status 404 se o funcionário não existir")
        void deveRetornarNotFoundAoBuscarIdInexistente() throws Exception {
            when(funcionarioService.listarPorId(99L))
                    .thenThrow(new FuncionarioNotFoundException("Funcionario não encontrado"));

            mockMvc.perform(get("/funcionario/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Testes do POST /funcionario")
    class CriarFuncionarioTest {

        @Test
        @DisplayName("Deve cadastrar um funcionário válido e retornar status 201 com Location")
        void deveCriarFuncionarioComSucesso() throws Exception {
            when(funcionarioService.cadastrar(any(FuncionarioRequestDTO.class)))
                    .thenReturn(new FuncionarioResponseDTO(2L, "Julia", "julia@empresa.com", null, null, null));

            mockMvc.perform(post("/funcionario")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(BODY_FUNCIONARIO))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/funcionario/2")))
                    .andExpect(jsonPath("$.id").value(2))
                    .andExpect(jsonPath("$.nome").value("Julia"));
        }

        @Test
        @DisplayName("Deve retornar 400 e não chamar o service quando o corpo é inválido")
        void deveRetornarBadRequestParaCorpoInvalido() throws Exception {
            mockMvc.perform(post("/funcionario")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verify(funcionarioService, never()).cadastrar(any());
        }
    }

    @Nested
    @DisplayName("Testes do PUT /funcionario/{id}")
    class AtualizarFuncionarioTest {

        @Test
        @DisplayName("Deve atualizar dados do funcionário com status 200")
        void deveAtualizarFuncionarioComSucesso() throws Exception {
            when(funcionarioService.atualizar(eq(1L), any(FuncionarioAtualizarRequestDTO.class)))
                    .thenReturn(funcionario(1L, "Julia Atualizada", "julia@empresa.com"));

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
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Julia Atualizada"));
        }

        @Test
        @DisplayName("Deve retornar 404 ao atualizar funcionário inexistente")
        void deveRetornarNotFoundAoAtualizarInexistente() throws Exception {
            when(funcionarioService.atualizar(eq(99L), any(FuncionarioAtualizarRequestDTO.class)))
                    .thenThrow(new FuncionarioNotFoundException("Funcionario não encontrado"));

            mockMvc.perform(put("/funcionario/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(BODY_FUNCIONARIO))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Testes do DELETE /funcionario/{id}")
    class DeletarFuncionarioTest {

        @Test
        @DisplayName("Deve deletar funcionário por ID e retornar status 204")
        void deveDeletarFuncionarioComSucesso() throws Exception {
            doNothing().when(funcionarioService).deletar(1L);

            mockMvc.perform(delete("/funcionario/1"))
                    .andExpect(status().isNoContent());

            verify(funcionarioService).deletar(1L);
        }

        @Test
        @DisplayName("Deve retornar status 404 ao tentar deletar id inexistente")
        void deveRetornarNotFoundAoDeletarInexistente() throws Exception {
            doThrow(new FuncionarioNotFoundException("Funcionario não encontrado"))
                    .when(funcionarioService).deletar(99L);

            mockMvc.perform(delete("/funcionario/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Testes do PATCH /funcionario/{id}/foto")
    class AtualizarFotoTest {

        private final MockMultipartFile arquivo = new MockMultipartFile(
                "file", "foto.png", MediaType.IMAGE_PNG_VALUE, "conteudo".getBytes());

        @Test
        @DisplayName("Deve atualizar a foto de perfil e retornar status 204")
        void deveAtualizarFotoComSucesso() throws Exception {
            doNothing().when(funcionarioService).atualizarFotoPerfil(eq(1L), any(MultipartFile.class));

            mockMvc.perform(multipart("/funcionario/1/foto")
                            .file(arquivo)
                            .with(request -> {
                                request.setMethod("PATCH");
                                return request;
                            }))
                    .andExpect(status().isNoContent());

            verify(funcionarioService).atualizarFotoPerfil(eq(1L), any(MultipartFile.class));
        }

        @Test
        @DisplayName("Deve retornar 404 ao atualizar foto de funcionário inexistente")
        void deveRetornarNotFoundAoAtualizarFotoInexistente() throws Exception {
            doThrow(new FuncionarioNotFoundException("Funcionario não encontrado"))
                    .when(funcionarioService).atualizarFotoPerfil(eq(99L), any(MultipartFile.class));

            mockMvc.perform(multipart("/funcionario/99/foto")
                            .file(arquivo)
                            .with(request -> {
                                request.setMethod("PATCH");
                                return request;
                            }))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 400 quando o parâmetro 'file' não é enviado")
        void deveRetornarBadRequestSemArquivo() throws Exception {
            mockMvc.perform(multipart("/funcionario/1/foto")
                            .with(request -> {
                                request.setMethod("PATCH");
                                return request;
                            }))
                    .andExpect(status().isBadRequest());

            verify(funcionarioService, never()).atualizarFotoPerfil(any(), any());
        }
    }
}