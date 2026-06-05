package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.config.GerenciadorTokenJwt;
import com.blessed.blsd_api_bend.dto.usuario.LoginRequestDTO;
import com.blessed.blsd_api_bend.dto.usuario.UsuarioTokenDTO;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private GerenciadorTokenJwt gerenciadorTokenJwt;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Nested
    @DisplayName("Testes do Fluxo de Autenticação com Sucesso")
    class AutenticarSucessoTest {

        @Test
        @DisplayName("Deve autenticar e retornar UsuarioTokenDTO com sucesso para um Cliente")
        void autenticarClienteComSucesso() {
            LoginRequestDTO loginDto = new LoginRequestDTO("user@email.com", "senha123");
            Authentication authMock = mock(Authentication.class);

            Acesso acessoCliente = new Acesso();
            acessoCliente.setId(1L);
            acessoCliente.setNome(TiposAcessos.CLIENTE);
            acessoCliente.setDescricao("Perfil de Acesso do Cliente");

            Cliente cliente = new Cliente();
            cliente.setId(1L);
            cliente.setNome("Gabriel");
            cliente.setEmail("user@email.com");
            cliente.setAcesso(acessoCliente);

            when(authenticationManager.authenticate(any())).thenReturn(authMock);
            when(gerenciadorTokenJwt.generateToken(authMock)).thenReturn("token-jwt-mockado");
            when(clienteRepository.findByEmail("user@email.com")).thenReturn(Optional.of(cliente));

            UsuarioTokenDTO resultado = usuarioService.autenticar(loginDto);

            assertNotNull(resultado);
            assertEquals("token-jwt-mockado", resultado.getToken());
            assertEquals("Gabriel", resultado.getNome());
            assertEquals("user@email.com", resultado.getEmail());
            assertEquals(1L, resultado.getId());
            assertEquals(acessoCliente, resultado.getAcesso());

            verify(funcionarioRepository, never()).findByEmail(anyString());
        }

        @Test
        @DisplayName("Deve buscar e retornar UsuarioTokenDTO com sucesso na tabela de Funcionários se não for Cliente")
        void autenticarFuncionarioComSucesso() {
            LoginRequestDTO loginDto = new LoginRequestDTO("func@email.com", "senha123");
            Authentication authMock = mock(Authentication.class);

            Acesso acessoFunc = new Acesso();
            acessoFunc.setId(2L);
            acessoFunc.setNome(TiposAcessos.FUNCIONARIO);
            acessoFunc.setDescricao("Perfil de Funcionário");

            Funcionario funcionario = new Funcionario();
            funcionario.setId(5L);
            funcionario.setNome("Lucas");
            funcionario.setEmail("func@email.com");
            funcionario.setAcesso(acessoFunc);
            funcionario.setConsulta(new ArrayList<>());

            when(authenticationManager.authenticate(any())).thenReturn(authMock);
            when(gerenciadorTokenJwt.generateToken(authMock)).thenReturn("token-jwt-mockado");
            when(clienteRepository.findByEmail("func@email.com")).thenReturn(Optional.empty());
            when(funcionarioRepository.findByEmail("func@email.com")).thenReturn(Optional.of(funcionario));

            UsuarioTokenDTO resultado = usuarioService.autenticar(loginDto);

            assertNotNull(resultado);
            assertEquals("token-jwt-mockado", resultado.getToken());
            assertEquals("Lucas", resultado.getNome());
            assertEquals("func@email.com", resultado.getEmail());
            assertEquals(5L, resultado.getId());
            assertEquals(acessoFunc, resultado.getAcesso());

            verify(clienteRepository, times(1)).findByEmail("func@email.com");
            verify(funcionarioRepository, times(1)).findByEmail("func@email.com");
        }
    }

    @Nested
    @DisplayName("Testes de Falha na Autenticação")
    class AutenticarFalhaTest {

        @Test
        @DisplayName("Deve propagar a exceção se o AuthenticationManager falhar nas credenciais")
        void deveLancarExcecaoQuandoCredenciaisInvalidas() {
            LoginRequestDTO loginDto = new LoginRequestDTO("errado@email.com", "senhaErrada");

            when(authenticationManager.authenticate(any())).thenThrow(new AuthenticationException("Invalido") {});

            assertThrows(AuthenticationException.class, () -> {
                usuarioService.autenticar(loginDto);
            });

            verify(clienteRepository, never()).findByEmail(anyString());
            verify(funcionarioRepository, never()).findByEmail(anyString());
            verify(gerenciadorTokenJwt, never()).generateToken(any());
        }
    }
}