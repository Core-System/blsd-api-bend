package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.funcionario.FuncionarioRequestDTO;
import com.blessed.blsd_api_bend.model.entity.Funcionario;
import com.blessed.blsd_api_bend.service.FuncionarioService;
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
class FuncionarioControllerTest {

    @Mock
    private FuncionarioService funcionarioService;

    @InjectMocks
    private FuncionarioController funcionarioController;

    @Test
    @DisplayName("Deve listar funcionarios com status 200 OK")
    void deveListarFuncionarios() {
        when(funcionarioService.listarTodos()).thenReturn(Collections.singletonList(new Funcionario()));

        ResponseEntity<List<Funcionario>> resposta = funcionarioController.listarFuncionarios();

        assertNotNull(resposta);
        assertEquals(200, resposta.getStatusCode().value());
    }

    @Test
    @DisplayName("Deve cadastrar funcionario e retornar status 201 Created")
    void deveCriarFuncionario() {
        FuncionarioRequestDTO dto = new FuncionarioRequestDTO();
        dto.setNome("Julia");

        Funcionario funcSalvo = new Funcionario();
        funcSalvo.setId(2L);

        when(funcionarioService.cadastrar(any(Funcionario.class))).thenReturn(funcSalvo);

        ResponseEntity<Funcionario> resposta = funcionarioController.criarFuncionario(dto);

        assertNotNull(resposta);
        assertEquals(201, resposta.getStatusCode().value());
    }
}