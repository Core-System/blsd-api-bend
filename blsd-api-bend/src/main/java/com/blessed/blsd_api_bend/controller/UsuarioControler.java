package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.usuario.LoginRequest;
import com.blessed.blsd_api_bend.exception.cliente.ClienteNotFoundException;
import com.blessed.blsd_api_bend.exception.funcionario.FuncionarioNotFoundException;
import com.blessed.blsd_api_bend.model.entity.Cliente;
import com.blessed.blsd_api_bend.model.entity.Funcionario;
import com.blessed.blsd_api_bend.service.ClienteService;
import com.blessed.blsd_api_bend.service.FuncionarioService;
import com.blessed.blsd_api_bend.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UsuarioControler {

    private final ClienteService clienteService;
    private final FuncionarioService funcionarioService;


    public UsuarioControler(ClienteService clienteService, FuncionarioService funcionarioService) {
        this.clienteService = clienteService;
        this.funcionarioService = funcionarioService;
    }


    @PostMapping("/login")
    public ResponseEntity<?> logar(@RequestBody LoginRequest loginRequest) {
        try {
            Cliente cliente = clienteService.buscarPorEmail(loginRequest.getEmail());
            if (cliente.getSenha().equals(loginRequest.getSenha())) {
                String token = JwtUtil.gerarToken(cliente.getEmail(),cliente.getAcesso().getNome().name());
                return ResponseEntity.ok(token);
            }
        } catch (ClienteNotFoundException e) {
        }


        try {
            Funcionario funcionario = funcionarioService.buscarPorEmail(loginRequest.getEmail());
            if (funcionario.getSenha().equals(loginRequest.getSenha())) {
                String token = JwtUtil.gerarToken(funcionario.getEmail(),funcionario.getAcesso().getNome().name()
                );
                return ResponseEntity.ok(token);
            }
        } catch (FuncionarioNotFoundException e) {
        }


        return ResponseEntity.status(401).body("Credenciais inválidas");
    }
}







