package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.exception.funcionario.FuncionarioAlreadyExistsException;
import com.blessed.blsd_api_bend.model.entity.Funcionario;
import com.blessed.blsd_api_bend.repository.FuncionarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private FuncionarioService funcionarioService;

    @Test
    @DisplayName("Deve lancar excecao apenas se e-mail E cpf ja existirem")
    void cadastrarFuncionarioDuplicado() {
        Funcionario func = new Funcionario();
        func.setEmail("func@empresa.com");
        func.setCpf("12345678900");

        when(funcionarioRepository.existsByEmail(func.getEmail())).thenReturn(true);
        when(funcionarioRepository.existsByCpf(func.getCpf())).thenReturn(true);

        assertThrows(FuncionarioAlreadyExistsException.class, () -> {
            funcionarioService.cadastrar(func);
        });
    }
}