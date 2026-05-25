package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.cliente.ClienteRequestDTO;
import com.blessed.blsd_api_bend.model.entity.Cliente;
import com.blessed.blsd_api_bend.service.ClienteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    @Test
    @DisplayName("Deve retornar lista de clientes com status 200 OK")
    void deveListarClientes() {
        when(clienteService.listarTodos()).thenReturn(Collections.singletonList(new Cliente()));

        ResponseEntity<List<Cliente>> resposta = clienteController.getCliente();

        assertNotNull(resposta);
        assertEquals(200, resposta.getStatusCode().value());
        assertNotNull(resposta.getBody());
    }

    @Test
    @DisplayName("Deve buscar cliente por ID com status 200 OK")
    void deveBuscarClientePorId() {
        Cliente clienteMock = new Cliente();
        clienteMock.setId(1L);
        clienteMock.setNome("Marcos");
        when(clienteService.listarPorId(1L)).thenReturn(clienteMock);

        ResponseEntity<Cliente> resposta = clienteController.getClienteById(1L);

        assertNotNull(resposta);
        assertEquals(200, resposta.getStatusCode().value());
        assertEquals("Marcos", resposta.getBody().getNome());
    }

    @Test
    @DisplayName("Deve cadastrar cliente e retornar status 201 Created")
    void deveCriarCliente() {
        ClienteRequestDTO dto = new ClienteRequestDTO();
        dto.setNome("Marcos");

        Cliente clienteSalvo = new Cliente();
        clienteSalvo.setId(1L);

        when(clienteService.cadastrar(any(Cliente.class))).thenReturn(clienteSalvo);

        ResponseEntity<Cliente> resposta = clienteController.criarCliente(dto);

        assertNotNull(resposta);
        assertEquals(201, resposta.getStatusCode().value());
    }
}