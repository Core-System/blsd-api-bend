package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.exception.cliente.ClienteAlreadyExistsException;
import com.blessed.blsd_api_bend.exception.cliente.ClienteNotFoundException;
import com.blessed.blsd_api_bend.model.entity.Cliente;
import com.blessed.blsd_api_bend.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService implements ICrudService<Cliente> {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente listarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado"));
    }

    public Cliente buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado"));
    }

    @Override
    public Cliente cadastrar(Cliente cliente) {
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new ClienteAlreadyExistsException("Cliente já existente");
        }
        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente atualizar(Long id, Cliente cliente) {
        return clienteRepository.findById(id).map(c -> {
            c.setNome(cliente.getNome());
            c.setEmail(cliente.getEmail());
            c.setSenha(cliente.getSenha());
            c.setUrlFoto(cliente.getUrlFoto());
            c.setDataCriacao(cliente.getDataCriacao());
            c.setDataNasc(cliente.getDataNasc());
            c.setTelefone(cliente.getTelefone());
            c.setAcesso(cliente.getAcesso());
            c.setEndereco(cliente.getEndereco());
            c.setConsulta(cliente.getConsulta());
            return clienteRepository.save(c);
        }).orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado"));
    }

    @Override
    public void deletar(Long id) {
        Cliente cliente = listarPorId(id);
        clienteRepository.delete(cliente);
    }
}

