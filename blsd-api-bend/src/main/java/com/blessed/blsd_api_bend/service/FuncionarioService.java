package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.exception.cliente.ClienteAlreadyExistsException;
import com.blessed.blsd_api_bend.exception.cliente.ClienteNotFoundException;
import com.blessed.blsd_api_bend.exception.funcionario.FuncionarioAlreadyExistsException;
import com.blessed.blsd_api_bend.exception.funcionario.FuncionarioNotFoundException;
import com.blessed.blsd_api_bend.model.entity.Cliente;
import com.blessed.blsd_api_bend.model.entity.Funcionario;
import com.blessed.blsd_api_bend.repository.ClienteRepository;
import com.blessed.blsd_api_bend.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;


    public FuncionarioService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Funcionario> listarFuncionarios(){
        return funcionarioRepository.findAll();
    }

    public Funcionario listarPorId(Long id){
        return funcionarioRepository.findById(id).orElseThrow(()-> new FuncionarioNotFoundException("Funcionario não encontrado"));
    }

    public Funcionario buscarPorEmail(String email){
        return funcionarioRepository.findByEmail(email).orElseThrow(()-> new FuncionarioNotFoundException("Funcionario não encontrado"));

    }



    public Funcionario cadastrarFuncionario (Funcionario funcionario){
        if(funcionarioRepository.existsByEmail(funcionario.getEmail()) && funcionarioRepository.existsByCpf(funcionario.getCpf())){
            throw new FuncionarioAlreadyExistsException("Funcionario já existente");
        }

        return funcionarioRepository.save(funcionario);
    }

    public Funcionario atualizarFuncionario (Long id, Funcionario funcionario){
        return funcionarioRepository.findById(id).map(funcionarioEncontrado -> {
            funcionarioEncontrado.setNome(funcionario.getNome());
            funcionarioEncontrado.setEmail(funcionario.getEmail());
            funcionarioEncontrado.setSenha(funcionario.getSenha());
            funcionarioEncontrado.setUrlFoto(funcionario.getUrlFoto());
            funcionarioEncontrado.setCpf(funcionario.getCpf());
            funcionarioEncontrado.setEmpresa(funcionario.getEmpresa());
            funcionarioEncontrado.setAcesso(funcionario.getAcesso());
            funcionarioEncontrado.setConsulta(funcionario.getConsulta());
            return funcionarioRepository.save(funcionarioEncontrado);
        }).orElseThrow(()-> new FuncionarioNotFoundException("Funcionario não encontrado"));
    }

    public void deletarFuncionario(Long id){
        Funcionario funcionario = funcionarioRepository.findById(id).orElseThrow(() -> new FuncionarioNotFoundException("Cliente não encontrado"));
        funcionarioRepository.delete(funcionario);
    }




}
