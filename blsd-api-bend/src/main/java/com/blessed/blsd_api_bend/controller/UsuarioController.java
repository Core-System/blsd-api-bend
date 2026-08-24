package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.cliente.ClienteResponseDTO;
import com.blessed.blsd_api_bend.dto.funcionario.FuncionarioResponseDTO;
import com.blessed.blsd_api_bend.dto.usuario.LoginRequestDTO;
import com.blessed.blsd_api_bend.dto.usuario.UsuarioMapper;
import com.blessed.blsd_api_bend.dto.usuario.UsuarioTokenDTO;
import com.blessed.blsd_api_bend.model.entity.Cliente;
import com.blessed.blsd_api_bend.model.entity.Funcionario;
import com.blessed.blsd_api_bend.service.ClienteService;
import com.blessed.blsd_api_bend.service.FuncionarioService;
import com.blessed.blsd_api_bend.service.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin("*")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final ClienteService clienteService;
    private final FuncionarioService funcionarioService;

    public UsuarioController(UsuarioService usuarioService, ClienteService clienteService, FuncionarioService funcionarioService) {
        this.usuarioService = usuarioService;
        this.clienteService = clienteService;
        this.funcionarioService = funcionarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioTokenDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        UsuarioTokenDTO usuarioTokenDto = usuarioService.autenticar(loginRequestDTO);
        return ResponseEntity.ok(usuarioTokenDto);
    }

    @GetMapping("/clientes")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<List<ClienteResponseDTO>> listarClientes() {
        List<ClienteResponseDTO> clientes = clienteService.listarTodos()
                .stream().map(UsuarioMapper::toResponseDTO).toList();
        if (clientes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/funcionarios")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<List<FuncionarioResponseDTO>> listarFuncionarios() {
        List<FuncionarioResponseDTO> funcionarios = funcionarioService.listarTodos()
                .stream().map(UsuarioMapper::toResponseDTO).toList();
        if (funcionarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(funcionarios);
    }

    @PostMapping("/link-agendamento")
    public ResponseEntity<UsuarioTokenDTO> redirecionarAgendamento(@RequestParam(name = "code") String tokenAgendameto) {
        UsuarioTokenDTO token = usuarioService.validarTokenAgendamento(tokenAgendameto);
        return ResponseEntity.ok(token);
    }
}




