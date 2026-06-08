package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.exception.cliente.ClienteAlreadyExistsException;
import com.blessed.blsd_api_bend.exception.cliente.ClienteNotFoundException;
import com.blessed.blsd_api_bend.model.entity.Cliente;
import com.blessed.blsd_api_bend.repository.ClienteRepository;
import com.blessed.blsd_api_bend.repository.ConsultaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClienteService clienteService;

    @Nested
    @DisplayName("Testes do Fluxo de Cadastro")
    class CadastrarClienteTest {

        @Test
        @DisplayName("Deve cadastrar cliente com sucesso quando o e-mail não existir")
        void cadastrarComSucesso() {
            Cliente cliente = new Cliente();
            cliente.setEmail("teste@email.com");
            cliente.setSenha("senha123");

            when(clienteRepository.existsByEmail(cliente.getEmail())).thenReturn(false);
            when(passwordEncoder.encode("senha123")).thenReturn("senhaCriptografada");
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Cliente resultado = clienteService.cadastrar(cliente);

            assertNotNull(resultado);
            assertEquals("senhaCriptografada", resultado.getSenha());
            verify(clienteRepository, times(1)).save(cliente);
        }

        @Test
        @DisplayName("Deve lançar exceção ao cadastrar cliente com e-mail já existente")
        void cadastrarComEmailExistenteLancaExcecao() {
            Cliente cliente = new Cliente();
            cliente.setEmail("existente@email.com");

            when(clienteRepository.existsByEmail(cliente.getEmail())).thenReturn(true);

            assertThrows(ClienteAlreadyExistsException.class, () -> {
                clienteService.cadastrar(cliente);
            });

            verify(clienteRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes de Busca por ID")
    class BuscarClienteTest {

        @Test
        @DisplayName("Deve lançar exceção quando buscar por ID inexistente")
        void buscarPorIdInexistenteLancaExcecao() {
            when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ClienteNotFoundException.class, () -> {
                clienteService.listarPorId(1L);
            });
        }

        @Test
        @DisplayName("Deve retornar Cliente quando o ID existir")
        void buscarPorIdComSucesso() {
            Cliente cliente = new Cliente();
            cliente.setId(1L);
            cliente.setNome("Lucas");

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

            Cliente resultado = clienteService.listarPorId(1L);

            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertEquals("Lucas", resultado.getNome());
        }
    }

    @Nested
    @DisplayName("Testes de Atualização e Remoção")
    class ModificarClienteTest {

        @Test
        @DisplayName("Deve atualizar os dados do cliente com sucesso quando ele existir")
        void atualizarComSucesso() {
            Cliente clienteExistente = new Cliente();
            clienteExistente.setId(1L);
            clienteExistente.setNome("Nome Antigo");
            clienteExistente.setEmail("antigo@email.com");

            Cliente novosDados = new Cliente();
            novosDados.setNome("Nome Novo");
            novosDados.setEmail("novo@email.com");

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExistente));
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Cliente resultado = clienteService.atualizar(1L, novosDados);

            assertNotNull(resultado);
            assertEquals("Nome Novo", resultado.getNome());
            assertEquals("novo@email.com", resultado.getEmail());
            verify(clienteRepository, times(1)).save(clienteExistente);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar atualizar cliente inexistente")
        void atualizarClienteInexistenteLancaExcecao() {
            Cliente novosDados = new Cliente();
            when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ClienteNotFoundException.class, () -> {
                clienteService.atualizar(99L, novosDados);
            });

            verify(clienteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve deletar cliente com sucesso se o ID existir")
        void deletarComSucesso() {
            Cliente cliente = new Cliente();
            cliente.setId(1L);

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

            assertDoesNotThrow(() -> clienteService.deletar(1L));

            verify(clienteRepository, times(1)).delete(cliente);
        }
    }
}