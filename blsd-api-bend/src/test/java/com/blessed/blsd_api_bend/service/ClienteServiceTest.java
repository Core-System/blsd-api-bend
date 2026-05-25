package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.exception.cliente.ClienteAlreadyExistsException;
import com.blessed.blsd_api_bend.exception.cliente.ClienteNotFoundException;
import com.blessed.blsd_api_bend.model.entity.Cliente;
import com.blessed.blsd_api_bend.repository.ClienteRepository;
import org.junit.jupiter.api.DisplayName;
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
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    @DisplayName("Deve cadastrar cliente com sucesso quando o email nao existir")
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
    @DisplayName("Deve lancar excecao ao cadastrar cliente com email ja existente")
    void cadastrarComEmailExistenteLancaExcecao() {
        Cliente cliente = new Cliente();
        cliente.setEmail("existente@email.com");

        when(clienteRepository.existsByEmail(cliente.getEmail())).thenReturn(true);

        assertThrows(ClienteAlreadyExistsException.class, () -> {
            clienteService.cadastrar(cliente);
        });

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lancar excecao quando buscar por ID inexistente")
    void buscarPorIdInexistenteLancaExcecao() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ClienteNotFoundException.class, () -> {
            clienteService.listarPorId(1L);
        });
    }
}