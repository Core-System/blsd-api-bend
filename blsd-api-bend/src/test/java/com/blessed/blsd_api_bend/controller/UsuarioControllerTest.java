package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.usuario.LoginRequestDTO;
import com.blessed.blsd_api_bend.dto.usuario.UsuarioTokenDTO;
import com.blessed.blsd_api_bend.service.ClienteService;
import com.blessed.blsd_api_bend.service.FuncionarioService;
import com.blessed.blsd_api_bend.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private ClienteService clienteService;

    @Mock
    private FuncionarioService funcionarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @Test
    @DisplayName("Deve realizar login e retornar status 200 OK com o token")
    void deveLogarComSucesso() {
        LoginRequestDTO request = new LoginRequestDTO("login@email.com", "123");
        UsuarioTokenDTO responseMock = new UsuarioTokenDTO(1L, "Admin", "login@email.com", "token-fake", null);

        when(usuarioService.autenticar(any(LoginRequestDTO.class))).thenReturn(responseMock);

        ResponseEntity<UsuarioTokenDTO> resposta = usuarioController.login(request);

        assertNotNull(resposta);
        assertEquals(200, resposta.getStatusCode().value());
        assertEquals("token-fake", resposta.getBody().getToken());
    }
}