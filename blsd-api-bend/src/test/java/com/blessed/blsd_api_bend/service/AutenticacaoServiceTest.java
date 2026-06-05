package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.model.entity.Acesso;
import com.blessed.blsd_api_bend.model.entity.Cliente;
import com.blessed.blsd_api_bend.model.entity.Funcionario;
import com.blessed.blsd_api_bend.model.enums.TiposAcessos;
import com.blessed.blsd_api_bend.repository.ClienteRepository;
import com.blessed.blsd_api_bend.repository.FuncionarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    @Nested
    @DisplayName("Testes de Carregamento de Usuário por Username (E-mail)")
    class LoadUserByUsernameTest {

        @Test
        @DisplayName("Deve retornar UserDetails do Cliente quando encontrar pelo e-mail")
        void deveAcharCliente() {
            Acesso acessoCliente = new Acesso();
            acessoCliente.setId(1L);
            acessoCliente.setNome(TiposAcessos.CLIENTE);

            Cliente cliente = new Cliente();
            cliente.setEmail("cliente@email.com");
            cliente.setSenha("senha");
            cliente.setAcesso(acessoCliente);

            when(clienteRepository.findByEmail("cliente@email.com")).thenReturn(Optional.of(cliente));

            UserDetails userDetails = autenticacaoService.loadUserByUsername("cliente@email.com");

            assertNotNull(userDetails);
            assertEquals("cliente@email.com", userDetails.getUsername());
            verify(funcionarioRepository, never()).findByEmail(anyString());
        }

        @Test
        @DisplayName("Deve buscar na tabela de funcionários se não encontrar na de clientes")
        void deveBuscarFuncionarioSeClienteNaoExistir() {
            Acesso acessoFunc = new Acesso();
            acessoFunc.setId(2L);
            acessoFunc.setNome(TiposAcessos.FUNCIONARIO);

            Funcionario funcionario = new Funcionario();
            funcionario.setEmail("func@email.com");
            funcionario.setSenha("senha");
            funcionario.setAcesso(acessoFunc);
            funcionario.setConsulta(new ArrayList<>());

            when(clienteRepository.findByEmail("func@email.com")).thenReturn(Optional.empty());
            when(funcionarioRepository.findByEmail("func@email.com")).thenReturn(Optional.of(funcionario));

            UserDetails userDetails = autenticacaoService.loadUserByUsername("func@email.com");

            assertNotNull(userDetails);
            assertEquals("func@email.com", userDetails.getUsername());
            verify(funcionarioRepository, times(1)).findByEmail("func@email.com");
        }

        @Test
        @DisplayName("Deve lançar UsernameNotFoundException se não encontrar em nenhuma das tabelas")
        void deveLancarExcecaoSeUsuarioNaoExistir() {
            when(clienteRepository.findByEmail("invalido@email.com")).thenReturn(Optional.empty());
            when(funcionarioRepository.findByEmail("invalido@email.com")).thenReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class, () -> {
                autenticacaoService.loadUserByUsername("invalido@email.com");
            });
        }
    }
}