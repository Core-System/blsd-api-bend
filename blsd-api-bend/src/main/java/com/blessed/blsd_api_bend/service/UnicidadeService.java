package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.exception.funcionario.FuncionarioAlreadyExistsException;
import com.blessed.blsd_api_bend.repository.ClienteRepository;
import com.blessed.blsd_api_bend.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UnicidadeService {

    private final FuncionarioRepository funcionarioRepository;
    private final ClienteRepository clienteRepository;

    public UnicidadeService(FuncionarioRepository funcionarioRepository, ClienteRepository clienteRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.clienteRepository = clienteRepository;
    }

    public void validarEmailUnico(String email, Long idIgnorado) {
        boolean emailEmFuncionario = funcionarioRepository.findByEmail(email)
                .map(f -> !f.getId().equals(idIgnorado))
                .orElseGet(() -> funcionarioRepository.existsByEmail(email));

        boolean emailEmCliente = clienteRepository.existsByEmail(email);

        if (emailEmFuncionario || emailEmCliente) {
            throw new FuncionarioAlreadyExistsException("Este e-mail já está em uso no sistema.");
        }
    }
}
