package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.cliente.ClienteAtualizarRequestDTO;
import com.blessed.blsd_api_bend.dto.cliente.ClienteRequestDTO;
import com.blessed.blsd_api_bend.dto.cliente.ClienteResponseDTO;
import com.blessed.blsd_api_bend.service.ClienteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    @Test
    @DisplayName("Deve listar clientes paginados com sucesso e retornar HTTP 200")
    void listarClientes_DeveRetornarPaginaDeClientes_QuandoPaginacaoFornecida() {
        Pageable pageable = PageRequest.of(0, 10);
        ClienteResponseDTO dto = mock(ClienteResponseDTO.class);
        Page<ClienteResponseDTO> paginaEsperada = new PageImpl<>(List.of(dto));

        when(clienteService.listarTodos(pageable)).thenReturn(paginaEsperada);

        ResponseEntity<Page<ClienteResponseDTO>> response = clienteController.listarClientes(pageable);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(clienteService, times(1)).listarTodos(pageable);
    }

    @Test
    @DisplayName("Deve buscar cliente por ID com sucesso e retornar HTTP 200")
    void buscarPorId_DeveRetornarClienteResponseDTO_QuandoIdExiste() {
        Long id = 1L;
        ClienteResponseDTO responseEsperada = mock(ClienteResponseDTO.class);
        when(clienteService.buscarPorId(id)).thenReturn(responseEsperada);

        ResponseEntity<ClienteResponseDTO> response = clienteController.buscarPorId(id);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseEsperada, response.getBody());
        verify(clienteService, times(1)).buscarPorId(id);
    }

    @Test
    @DisplayName("Deve criar cliente com sucesso, retornar HTTP 201 e URI no cabeçalho Location")
    void criarCliente_DeveRetornar201ECriarLocationHeader_QuandoDadosValidos() {
        MockHttpServletRequest requestContext = new MockHttpServletRequest();
        requestContext.setRequestURI("/cliente");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(requestContext));

        ClienteRequestDTO requestDTO = mock(ClienteRequestDTO.class);
        ClienteResponseDTO responseDTO = mock(ClienteResponseDTO.class);
        when(responseDTO.getId()).thenReturn(1L);
        when(clienteService.cadastrar(requestDTO)).thenReturn(responseDTO);

        ResponseEntity<ClienteResponseDTO> response = clienteController.criarCliente(requestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        assertNotNull(response.getHeaders().getLocation());
        assertTrue(response.getHeaders().getLocation().getPath().endsWith("/cliente/1"));
        verify(clienteService, times(1)).cadastrar(requestDTO);

        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso e retornar HTTP 200")
    void atualizarCliente_DeveRetornarClienteResponseDTO_QuandoAtualizacaoSucesso() {
        Long id = 1L;
        ClienteAtualizarRequestDTO dto = mock(ClienteAtualizarRequestDTO.class);
        ClienteResponseDTO responseEsperada = mock(ClienteResponseDTO.class);
        when(clienteService.atualizar(id, dto)).thenReturn(responseEsperada);

        ResponseEntity<ClienteResponseDTO> response = clienteController.atualizarCliente(id, dto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseEsperada, response.getBody());
        verify(clienteService, times(1)).atualizar(id, dto);
    }

    @Test
    @DisplayName("Deve deletar cliente com sucesso e retornar HTTP 204 No Content")
    void deletarCliente_DeveRetornar204_QuandoIdValido() {
        Long id = 1L;
        doNothing().when(clienteService).deletar(id);

        ResponseEntity<Void> response = clienteController.deletarCliente(id);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(clienteService, times(1)).deletar(id);
    }

    @Test
    @DisplayName("Deve atualizar foto do perfil com sucesso e retornar HTTP 204 No Content")
    void atualizarFoto_DeveRetornar204_QuandoArquivoEnviado() {
        Long id = 1L;
        MultipartFile file = mock(MultipartFile.class);
        doNothing().when(clienteService).atualizarFotoPerfil(id, file);

        ResponseEntity<Void> response = clienteController.atualizarFoto(id, file);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(clienteService, times(1)).atualizarFotoPerfil(id, file);
    }
}