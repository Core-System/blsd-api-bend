package com.blessed.blsd_api_bend.config;

import com.blessed.blsd_api_bend.model.entity.Acesso;
import com.blessed.blsd_api_bend.model.entity.Funcionario;
import com.blessed.blsd_api_bend.model.enums.TiposAcessos;
import com.blessed.blsd_api_bend.repository.AcessoRepository;
import com.blessed.blsd_api_bend.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class UsuarioRaizInicializador implements CommandLineRunner {

    private final FuncionarioRepository funcionarioRepository;
    private final AcessoRepository acessoRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${root.user.name:}")
    private String nome;

    @Value("${root.user.email:}")
    private String email;

    @Value("${root.user.password:}")
    private String senha;

    public UsuarioRaizInicializador(FuncionarioRepository funcionarioRepository,
                                     AcessoRepository acessoRepository,
                                     PasswordEncoder passwordEncoder) {
        this.funcionarioRepository = funcionarioRepository;
        this.acessoRepository = acessoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (email.isBlank() || senha.isBlank()) {
            return;
        }

        if (funcionarioRepository.findByEmail(email).isPresent()) {
            return;
        }

        Acesso gestor = acessoRepository.findByNome(TiposAcessos.GESTOR)
                .orElseThrow(() -> new IllegalStateException(
                        "Acesso GESTOR não inicializado pelo Flyway."));

        Funcionario usuarioRaiz = new Funcionario();
        usuarioRaiz.setNome(nome.isBlank() ? "Gestor inicial" : nome);
        usuarioRaiz.setEmail(email);
        usuarioRaiz.setSenha(passwordEncoder.encode(senha));
        usuarioRaiz.setDataCriacao(LocalDateTime.now());
        usuarioRaiz.setAcesso(gestor);

        funcionarioRepository.save(usuarioRaiz);
    }
}
