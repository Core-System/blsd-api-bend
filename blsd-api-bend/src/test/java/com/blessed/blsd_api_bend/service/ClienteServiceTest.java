package com.blessed.blsd_api_bend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import com.blessed.blsd_api_bend.dto.cliente.ClienteAtualizarRequestDTO;
import com.blessed.blsd_api_bend.dto.cliente.ClienteRequestDTO;
import com.blessed.blsd_api_bend.dto.cliente.ClienteResponseDTO;
import com.blessed.blsd_api_bend.exception.cliente.ClienteAlreadyExistsException;
import com.blessed.blsd_api_bend.exception.cliente.ClienteNotFoundException;
import com.blessed.blsd_api_bend.model.entity.Acesso;
import com.blessed.blsd_api_bend.model.entity.Cliente;
import com.blessed.blsd_api_bend.model.entity.Consulta;
import com.blessed.blsd_api_bend.model.enums.TiposAcessos;
import com.blessed.blsd_api_bend.repository.AcessoRepository;
import com.blessed.blsd_api_bend.repository.ClienteRepository;
import com.blessed.blsd_api_bend.repository.ConsultaRepository;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    private static final String EMAIL = "lucas@email.com";
    private static final String TELEFONE = "11999999999";

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private AcessoRepository acessoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private UnicidadeService unicidadeService;

    @InjectMocks
    private ClienteService clienteService;


    private Acesso criarAcessoCliente() {
        Acesso acesso = new Acesso();
        acesso.setId(1L);
        acesso.setNome(TiposAcessos.CLIENTE);
        return acesso;
    }

    private Cliente criarCliente(Long id, String nome, String email) {
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setNome(nome);
        cliente.setEmail(email);
        cliente.setAcesso(criarAcessoCliente());
        return cliente;
    }

    private ClienteRequestDTO criarCadastroDTO() {
        ClienteRequestDTO dto = mock(ClienteRequestDTO.class);
        lenient().when(dto.getEmail()).thenReturn(EMAIL);
        lenient().when(dto.getTelefone()).thenReturn(TELEFONE);
        lenient().when(dto.getSenha()).thenReturn("senha123");
        return dto;
    }


    @Nested
    @DisplayName("Testes de Listagem e Busca")
    class BuscarClienteTest {

        @Test
        @DisplayName("Deve listar clientes paginados convertendo para DTO")
        void deveListarTodosComSucesso() {
            Pageable pageable = PageRequest.of(0, 10);
            Cliente cliente = criarCliente(1L, "Lucas", EMAIL);
            when(clienteRepository.findAll(pageable))
                    .thenReturn(new PageImpl<>(List.of(cliente), pageable, 1));

            Page<ClienteResponseDTO> resultado = clienteService.listarTodos(pageable);

            assertEquals(1, resultado.getTotalElements());
            assertEquals(1L, resultado.getContent().get(0).getId());
            assertEquals("Lucas", resultado.getContent().get(0).getNome());
        }

        @Test
        @DisplayName("Deve retornar o cliente em DTO quando o ID existir")
        void deveBuscarPorIdComSucesso() {
            when(clienteRepository.findById(1L))
                    .thenReturn(Optional.of(criarCliente(1L, "Lucas", EMAIL)));

            ClienteResponseDTO resultado = clienteService.buscarPorId(1L);

            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertEquals("Lucas", resultado.getNome());
        }

        @Test
        @DisplayName("Deve lançar ClienteNotFoundException quando o ID não existir")
        void deveLancarExcecaoQuandoIdInexistente() {
            when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

            ClienteNotFoundException ex = assertThrows(ClienteNotFoundException.class,
                    () -> clienteService.buscarPorId(1L));

            assertEquals("Cliente com ID 1 não encontrado.", ex.getMessage());
        }
    }


    @Nested
    @DisplayName("Testes do Fluxo de Cadastro")
    class CadastrarClienteTest {

        @Test
        @DisplayName("Deve cadastrar cliente: valida e-mail, criptografa senha e atribui acesso CLIENTE")
        void deveCadastrarComSucesso() {
            Acesso acessoCliente = criarAcessoCliente();
            ClienteRequestDTO dto = criarCadastroDTO();

            when(clienteRepository.existsByTelefone(TELEFONE)).thenReturn(false);
            when(passwordEncoder.encode("senha123")).thenReturn("senhaCriptografada");
            when(acessoRepository.findByNome(TiposAcessos.CLIENTE)).thenReturn(Optional.of(acessoCliente));
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

            ClienteResponseDTO resultado = clienteService.cadastrar(dto);

            assertNotNull(resultado);

            ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
            verify(clienteRepository).save(captor.capture());
            Cliente salvo = captor.getValue();
            assertEquals("senhaCriptografada", salvo.getSenha());
            assertSame(acessoCliente, salvo.getAcesso());
            assertEquals(EMAIL, salvo.getEmail());

            verify(unicidadeService).validarEmailUnico(EMAIL, null);
        }

        @Test
        @DisplayName("Deve propagar a exceção quando o e-mail já está em uso")
        void deveLancarExcecaoQuandoEmailJaExiste() {
            ClienteRequestDTO dto = criarCadastroDTO();
            doThrow(new ClienteAlreadyExistsException("E-mail já está em uso."))
                    .when(unicidadeService).validarEmailUnico(EMAIL, null);

            assertThrows(ClienteAlreadyExistsException.class, () -> clienteService.cadastrar(dto));

            verifyNoInteractions(clienteRepository, passwordEncoder, acessoRepository);
        }

        @Test
        @DisplayName("Deve lançar ClienteAlreadyExistsException quando o telefone já está em uso")
        void deveLancarExcecaoQuandoTelefoneJaExiste() {
            ClienteRequestDTO dto = criarCadastroDTO();
            when(clienteRepository.existsByTelefone(TELEFONE)).thenReturn(true);

            ClienteAlreadyExistsException ex = assertThrows(ClienteAlreadyExistsException.class,
                    () -> clienteService.cadastrar(dto));

            assertEquals("Este telefone já está em uso.", ex.getMessage());
            verify(clienteRepository, never()).save(any());
            verifyNoInteractions(passwordEncoder);
        }

        @Test
        @DisplayName("Deve lançar IllegalStateException quando o acesso CLIENTE não foi inicializado")
        void deveLancarExcecaoQuandoAcessoClienteNaoInicializado() {
            ClienteRequestDTO dto = criarCadastroDTO();
            when(clienteRepository.existsByTelefone(TELEFONE)).thenReturn(false);
            when(acessoRepository.findByNome(TiposAcessos.CLIENTE)).thenReturn(Optional.empty());

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> clienteService.cadastrar(dto));

            assertEquals("Acesso CLIENTE não inicializado no sistema.", ex.getMessage());
            verify(clienteRepository, never()).save(any());
        }
    }


    @Nested
    @DisplayName("Testes de Atualização e Remoção")
    class ModificarClienteTest {

        @Test
        @DisplayName("Deve atualizar os dados do cliente quando ele existir")
        void deveAtualizarComSucesso() {
            Cliente existente = criarCliente(1L, "Nome Antigo", "antigo@email.com");
            ClienteAtualizarRequestDTO dto = mock(ClienteAtualizarRequestDTO.class);
            when(dto.getNome()).thenReturn("Nome Novo");
            when(dto.getEmail()).thenReturn("novo@email.com");
            when(dto.getTelefone()).thenReturn(TELEFONE);
            when(dto.getUrlFoto()).thenReturn("/uploads/foto.png");

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

            ClienteResponseDTO resultado = clienteService.atualizar(1L, dto);

            assertNotNull(resultado);
            assertEquals("Nome Novo", existente.getNome());
            assertEquals("novo@email.com", existente.getEmail());
            assertEquals(TELEFONE, existente.getTelefone());
            assertEquals("/uploads/foto.png", existente.getUrlFoto());
            verify(unicidadeService).validarEmailUnico("novo@email.com", 1L);
            verify(clienteRepository).save(existente);
        }

        @Test
        @DisplayName("Deve lançar ClienteNotFoundException ao atualizar cliente inexistente")
        void deveLancarExcecaoAoAtualizarInexistente() {
            ClienteAtualizarRequestDTO dto = mock(ClienteAtualizarRequestDTO.class);
            when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ClienteNotFoundException.class, () -> clienteService.atualizar(99L, dto));

            verifyNoInteractions(unicidadeService);
            verify(clienteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve propagar a exceção e não salvar quando o novo e-mail já pertence a outro usuário")
        void deveLancarExcecaoAoAtualizarComEmailEmUso() {
            Cliente existente = criarCliente(1L, "Lucas", EMAIL);
            ClienteAtualizarRequestDTO dto = mock(ClienteAtualizarRequestDTO.class);
            when(dto.getEmail()).thenReturn("outro@email.com");

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
            doThrow(new ClienteAlreadyExistsException("E-mail já está em uso."))
                    .when(unicidadeService).validarEmailUnico("outro@email.com", 1L);

            assertThrows(ClienteAlreadyExistsException.class, () -> clienteService.atualizar(1L, dto));

            assertEquals(EMAIL, existente.getEmail());
            verify(clienteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve apagar as consultas do cliente e depois o cliente")
        void deveDeletarComSucesso() {
            Cliente cliente = criarCliente(1L, "Lucas", EMAIL);
            List<Consulta> consultas = List.of(new Consulta(), new Consulta());
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(consultaRepository.findByClienteId(1L)).thenReturn(consultas);

            clienteService.deletar(1L);

            InOrder ordem = inOrder(consultaRepository, clienteRepository);
            ordem.verify(consultaRepository).deleteAll(consultas);
            ordem.verify(clienteRepository).delete(cliente);
        }

        @Test
        @DisplayName("Deve lançar ClienteNotFoundException ao deletar cliente inexistente")
        void deveLancarExcecaoAoDeletarInexistente() {
            when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ClienteNotFoundException.class, () -> clienteService.deletar(99L));

            verifyNoInteractions(consultaRepository);
            verify(clienteRepository, never()).delete(any());
        }
    }


    @Nested
    @DisplayName("Testes de Atualização da Foto de Perfil")
    class AtualizarFotoPerfilTest {

        @Test
        @DisplayName("Deve armazenar o arquivo e salvar a URL /uploads/... no cliente")
        void deveAtualizarFotoComSucesso() {
            Cliente cliente = criarCliente(1L, "Lucas", EMAIL);
            MultipartFile file = mock(MultipartFile.class);
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(fileStorageService.armazenarArquivo(file)).thenReturn("abc123/foto.png");

            clienteService.atualizarFotoPerfil(1L, file);

            assertEquals("/uploads/abc123/foto.png", cliente.getUrlFoto());
            verify(clienteRepository).save(cliente);
        }

        @Test
        @DisplayName("Deve lançar exceção e não armazenar arquivo quando o cliente não existe")
        void deveLancarExcecaoQuandoClienteNaoEncontrado() {
            MultipartFile file = mock(MultipartFile.class);
            when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> clienteService.atualizarFotoPerfil(99L, file));

            assertEquals("Cliente não encontrado.", ex.getMessage());
            verifyNoInteractions(fileStorageService);
            verify(clienteRepository, never()).save(any());
        }
    }
}