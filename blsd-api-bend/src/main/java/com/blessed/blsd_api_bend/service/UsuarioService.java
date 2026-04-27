package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.config.GerenciadorTokenJwt;
import com.blessed.blsd_api_bend.dto.usuario.LoginRequestDTO;
import com.blessed.blsd_api_bend.dto.usuario.UsuarioTokenDTO;
import com.blessed.blsd_api_bend.model.entity.Usuario;
import com.blessed.blsd_api_bend.repository.ClienteRepository;
import com.blessed.blsd_api_bend.repository.FuncionarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final AuthenticationManager authenticationManager;
    private final GerenciadorTokenJwt gerenciadorTokenJwt;
    private final ClienteRepository clienteRepository;
    private final FuncionarioRepository funcionarioRepository;

    public UsuarioService(AuthenticationManager authenticationManager,
                          GerenciadorTokenJwt gerenciadorTokenJwt,
                          ClienteRepository clienteRepository,
                          FuncionarioRepository funcionarioRepository) {
        this.authenticationManager = authenticationManager;
        this.gerenciadorTokenJwt = gerenciadorTokenJwt;
        this.clienteRepository = clienteRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    public UsuarioTokenDTO autenticar(LoginRequestDTO loginDto) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getSenha());

        Authentication authentication = authenticationManager.authenticate(authToken);

        String token = gerenciadorTokenJwt.generateToken(authentication);

        Usuario usuario = clienteRepository.findByEmail(loginDto.getEmail())
                .map(c -> (Usuario) c)
                .orElseGet(() -> funcionarioRepository.findByEmail(loginDto.getEmail())
                        .map(f -> (Usuario) f)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado")));

        return new UsuarioTokenDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                token,
                usuario.getAcesso()
        );
    }
}