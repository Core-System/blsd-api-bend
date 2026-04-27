package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.exception.funcionario.FuncionarioAlreadyExistsException;
import com.blessed.blsd_api_bend.exception.funcionario.FuncionarioNotFoundException;
import com.blessed.blsd_api_bend.model.entity.Funcionario;
import com.blessed.blsd_api_bend.repository.FuncionarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuncionarioService implements UsuarioService<Funcionario> {

    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    @Override
    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAll();
    }

    @Override
    public Funcionario listarPorId(Long id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new FuncionarioNotFoundException("Funcionario não encontrado"));
    }

    @Override
    public Funcionario buscarPorEmail(String email) {
        return funcionarioRepository.findByEmail(email)
                .orElseThrow(() -> new FuncionarioNotFoundException("Funcionario não encontrado"));
    }

    @Override
    public Funcionario cadastrar(Funcionario funcionario) {
        if (funcionarioRepository.existsByEmail(funcionario.getEmail())
                && funcionarioRepository.existsByCpf(funcionario.getCpf())) {
            throw new FuncionarioAlreadyExistsException("Funcionario já existente");
        }
        return funcionarioRepository.save(funcionario);
    }

    @Override
    public Funcionario atualizar(Long id, Funcionario funcionario) {
        return funcionarioRepository.findById(id).map(f -> {
            f.setNome(funcionario.getNome());
            f.setEmail(funcionario.getEmail());
            f.setSenha(funcionario.getSenha());
            f.setUrlFoto(funcionario.getUrlFoto());
            f.setCpf(funcionario.getCpf());
            f.setEmpresa(funcionario.getEmpresa());
            f.setAcesso(funcionario.getAcesso());
            f.setConsulta(funcionario.getConsulta());
            return funcionarioRepository.save(f);
        }).orElseThrow(() -> new FuncionarioNotFoundException("Funcionario não encontrado"));
    }

    @Override
    public void deletar(Long id) {
        Funcionario funcionario = listarPorId(id);
        funcionarioRepository.delete(funcionario);
    }
}
