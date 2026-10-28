package com.blessed.blsd_api_bend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.blessed.blsd_api_bend.dto.usuario.UsuarioDetalheDTO;
import com.blessed.blsd_api_bend.exception.usuario.UsuarioNotFoundException;
import com.blessed.blsd_api_bend.model.entity.Acesso;
import com.blessed.blsd_api_bend.model.entity.Cliente;
import com.blessed.blsd_api_bend.model.entity.Funcionario;
import com.blessed.blsd_api_bend.model.enums.TiposAcessos;
import com.blessed.blsd_api_bend.repository.ClienteRepository;
import com.blessed.blsd_api_bend.repository.FuncionarioRepository;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    private Cliente criarCliente(String email) {
        Acesso acesso = new Acesso();
        acesso.setId(1L);
        acesso.setNome(TiposAcessos.CLIENTE);

        Cliente cliente = new Cliente();
        cliente.setEmail(email);
        cliente.setSenha("senha");
        cliente.setAcesso(acesso);
        return cliente;
    }

    private Funcionario criarFuncionario(String email) {
        Acesso acesso = new Acesso();
        acesso.setId(2L);
        acesso.setNome(TiposAcessos.FUNCIONARIO);

        Funcionario funcionario = new Funcionario();
        funcionario.setEmail(email);
        funcionario.setSenha("senha");
        funcionario.setAcesso(acesso);
        return funcionario;
    }

    @Nested
    @DisplayName("Testes de Carregamento de Usuário por Username (E-mail)")
    class LoadUserByUsernameTest {

        @Test
        @DisplayName("Deve retornar UserDetails do Cliente quando encontrar pelo e-mail")
        void deveAcharCliente() {
            when(clienteRepository.findByEmail("cliente@email.com"))
                    .thenReturn(Optional.of(criarCliente("cliente@email.com")));

            UserDetails userDetails = autenticacaoService.loadUserByUsername("cliente@email.com");

            assertNotNull(userDetails);
            assertInstanceOf(UsuarioDetalheDTO.class, userDetails);
            assertEquals("cliente@email.com", userDetails.getUsername());
            verify(clienteRepository, times(1)).findByEmail("cliente@email.com");
            verify(funcionarioRepository, never()).findByEmail(anyString());
        }

        @Test
        @DisplayName("Deve buscar na tabela de funcionários se não encontrar na de clientes")
        void deveBuscarFuncionarioSeClienteNaoExistir() {
            when(clienteRepository.findByEmail("func@email.com")).thenReturn(Optional.empty());
            when(funcionarioRepository.findByEmail("func@email.com"))
                    .thenReturn(Optional.of(criarFuncionario("func@email.com")));

            UserDetails userDetails = autenticacaoService.loadUserByUsername("func@email.com");

            assertNotNull(userDetails);
            assertInstanceOf(UsuarioDetalheDTO.class, userDetails);
            assertEquals("func@email.com", userDetails.getUsername());
            verify(clienteRepository, times(1)).findByEmail("func@email.com");
            verify(funcionarioRepository, times(1)).findByEmail("func@email.com");
        }

        @Test
        @DisplayName("Deve priorizar o cliente quando o e-mail existe nas duas tabelas")
        void devePriorizarClienteQuandoEmailExisteNosDois() {
            when(clienteRepository.findByEmail("duplicado@email.com"))
                    .thenReturn(Optional.of(criarCliente("duplicado@email.com")));

            UserDetails userDetails = autenticacaoService.loadUserByUsername("duplicado@email.com");

            assertEquals("duplicado@email.com", userDetails.getUsername());
            verify(funcionarioRepository, never()).findByEmail(anyString());
        }

        @Test
        @DisplayName("Deve lançar UsuarioNotFoundException se não encontrar em nenhuma das tabelas")
        void deveLancarExcecaoSeUsuarioNaoExistir() {
            when(clienteRepository.findByEmail("invalido@email.com")).thenReturn(Optional.empty());
            when(funcionarioRepository.findByEmail("invalido@email.com")).thenReturn(Optional.empty());

            UsuarioNotFoundException ex = assertThrows(UsuarioNotFoundException.class,
                    () -> autenticacaoService.loadUserByUsername("invalido@email.com"));

            assertEquals("usuario: invalido@email.com não encontrado", ex.getMessage());
            verify(clienteRepository, times(1)).findByEmail("invalido@email.com");
            verify(funcionarioRepository, times(1)).findByEmail("invalido@email.com");
        }
    }
}