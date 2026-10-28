package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.dto.usuario.UsuarioDetalheDTO;
import com.blessed.blsd_api_bend.exception.usuario.UsuarioNotFoundException;
import com.blessed.blsd_api_bend.repository.ClienteRepository;
import com.blessed.blsd_api_bend.repository.FuncionarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService implements UserDetailsService {

    private final ClienteRepository clienteRepository;
    private final FuncionarioRepository funcionarioRepository;

    public AutenticacaoService(ClienteRepository clienteRepository, FuncionarioRepository funcionarioRepository) {
        this.clienteRepository = clienteRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsuarioNotFoundException {
        return clienteRepository.findByEmail(username)
                .map(UsuarioDetalheDTO::new )
                .orElseGet(() -> funcionarioRepository.findByEmail(username)
                        .map(UsuarioDetalheDTO::new )
                        .orElseThrow(() -> new UsuarioNotFoundException(
                                String.format("usuario: %s não encontrado", username))) );
    }
}