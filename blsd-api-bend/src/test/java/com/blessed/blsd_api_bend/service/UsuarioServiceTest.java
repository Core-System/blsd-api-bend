package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.config.GerenciadorTokenJwt;
import com.blessed.blsd_api_bend.dto.usuario.LoginRequestDTO;
import com.blessed.blsd_api_bend.dto.usuario.UsuarioTokenDTO;
import com.blessed.blsd_api_bend.model.entity.Acesso;
import com.blessed.blsd_api_bend.model.entity.Cliente;
import com.blessed.blsd_api_bend.model.enums.TiposAcessos;
import com.blessed.blsd_api_bend.repository.ClienteRepository;
import com.blessed.blsd_api_bend.repository.FuncionarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

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

    @Test
    @DisplayName("Deve autenticar e retornar UsuarioTokenDTO com sucesso para um Cliente")
    void autenticarComSucesso() {
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
}