package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.config.GerenciadorTokenJwt;
import com.blessed.blsd_api_bend.dto.usuario.LoginRequestDTO;
import com.blessed.blsd_api_bend.dto.usuario.UsuarioTokenDTO;
import com.blessed.blsd_api_bend.exception.cliente.ClienteNotFoundException;
import com.blessed.blsd_api_bend.model.entity.Cliente;
import com.blessed.blsd_api_bend.model.entity.Usuario;
import com.blessed.blsd_api_bend.repository.ClienteRepository;
import com.blessed.blsd_api_bend.repository.FuncionarioRepository;
import com.google.auth.oauth2.JwtProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    public UsuarioTokenDTO validarTokenAgendamento(String tokenAgendamento) {
        Cliente clienteEncontrado = this.clienteRepository.findByTokenAgendamento(tokenAgendamento)
                .orElseThrow(() -> new ClienteNotFoundException("Token inválido ou usuário não encontrado"));

        if (clienteEncontrado.getTokenAgendamento() == null) {
            throw new RuntimeException("Este token já foi utilizado");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(clienteEncontrado.getExpiracaoTokenAgendamento())) {
            throw new RuntimeException("Token expirado");
        }

        if (!tokenAgendamento.equals(clienteEncontrado.getTokenAgendamento())) {
            throw new RuntimeException("Token inválido");
        }

        String token = gerenciadorTokenJwt.generateToken(
                clienteEncontrado.getEmail(),
                clienteEncontrado.getAcesso(),
                clienteEncontrado.getId()
        );

        clienteEncontrado.invalidarToken();
        this.clienteRepository.save(clienteEncontrado);

        return new UsuarioTokenDTO(
                clienteEncontrado.getId(),
                clienteEncontrado.getNome(),
                clienteEncontrado.getEmail(),
                token,
                clienteEncontrado.getAcesso()
        );
    }
}