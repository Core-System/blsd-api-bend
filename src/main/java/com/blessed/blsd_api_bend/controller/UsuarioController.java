package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.cliente.ClienteResponseDTO;
import com.blessed.blsd_api_bend.dto.funcionario.FuncionarioResponseDTO;
import com.blessed.blsd_api_bend.dto.usuario.LoginRequestDTO;
import com.blessed.blsd_api_bend.dto.usuario.UsuarioMapper;
import com.blessed.blsd_api_bend.dto.usuario.UsuarioTokenDTO;
import com.blessed.blsd_api_bend.service.ClienteService;
import com.blessed.blsd_api_bend.service.FuncionarioService;
import com.blessed.blsd_api_bend.service.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
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
        return ResponseEntity.ok(usuarioService.autenticar(loginRequestDTO));
    }

    @GetMapping("/clientes")
    @SecurityRequirement(name = "Bearer")
    @PreAuthorize("hasAuthority('GESTOR')")
    public ResponseEntity<Page<ClienteResponseDTO>> listarClientes(Pageable pageable) {
        return ResponseEntity.ok(clienteService.listarTodos(pageable));
    }

    @GetMapping("/funcionarios")
    @SecurityRequirement(name = "Bearer")
    @PreAuthorize("hasAuthority('GESTOR')")
    public ResponseEntity<List<FuncionarioResponseDTO>> listarFuncionarios() {
        //refatorar depois
        List<FuncionarioResponseDTO> funcionarios = funcionarioService.listarTodos()
                .stream().map(UsuarioMapper::toResponseDTO).toList();

        if (funcionarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(funcionarios);
    }

    @PostMapping("/link-agendamento")
    public ResponseEntity<UsuarioTokenDTO> redirecionarAgendamento(@RequestParam(name = "code") String tokenAgendamento) {
        return ResponseEntity.ok(usuarioService.validarTokenAgendamento(tokenAgendamento));
    }
}