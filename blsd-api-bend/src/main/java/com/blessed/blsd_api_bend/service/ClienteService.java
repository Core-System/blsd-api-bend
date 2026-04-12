package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.exception.cliente.ClienteAlreadyExistsException;
import com.blessed.blsd_api_bend.exception.cliente.ClienteNotFoundException;
import com.blessed.blsd_api_bend.model.entity.Cliente;
import com.blessed.blsd_api_bend.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;


    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarClientes(){
        return clienteRepository.findAll();
    }

    public Cliente listarPorId(Long id){
        return clienteRepository.findById(id).orElseThrow(()-> new ClienteNotFoundException("Cliente não encontrado"));
    }



    public Cliente cadastrarCliente (Cliente cliente){
        if(clienteRepository.existsByEmail(cliente.getEmail())){
            throw new ClienteAlreadyExistsException("Cliente já existente");
        }

        return clienteRepository.save(cliente);
    }

    public Cliente atualizarCliente (Long id, Cliente cliente){
        return clienteRepository.findById(id).map(clienteEncontrado -> {
            clienteEncontrado.setNome(cliente.getNome());
            clienteEncontrado.setEmail(cliente.getEmail());
            clienteEncontrado.setSenha(cliente.getSenha());
            clienteEncontrado.setUrlFoto(cliente.getUrlFoto());
            clienteEncontrado.setDataCriacao(cliente.getDataCriacao());
            clienteEncontrado.setDataNasc(cliente.getDataNasc());
            clienteEncontrado.setTelefone(cliente.getTelefone());
            clienteEncontrado.setAcesso(cliente.getAcesso());
            clienteEncontrado.setEndereco(cliente.getEndereco());
            clienteEncontrado.setConsulta(cliente.getConsulta());
            return clienteRepository.save(clienteEncontrado);
        }).orElseThrow(()-> new ClienteNotFoundException("Cliente não encontrado"));
    }

    public void deletarCliente(Long id){
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado"));
        clienteRepository.delete(cliente);
    }



}
