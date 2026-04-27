package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.funcionario.FuncionarioRequestDTO;
import com.blessed.blsd_api_bend.model.entity.Funcionario;
import com.blessed.blsd_api_bend.service.FuncionarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcionario")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @GetMapping
    public ResponseEntity<List<Funcionario>> listarFuncionarios() {
        List<Funcionario> funcionarios = funcionarioService.listarTodos();
        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Funcionario> listarPorId(@PathVariable Long id) {
        Funcionario funcionario = funcionarioService.listarPorId(id);
        return ResponseEntity.ok(funcionario);
    }

    @PostMapping
    public ResponseEntity<Funcionario> criarFuncionario(@Valid @RequestBody FuncionarioRequestDTO funcionarioDTO) {
        Funcionario funcionarioCriado = new Funcionario();

        funcionarioCriado.setNome(funcionarioDTO.getNome());
        funcionarioCriado.setEmail(funcionarioDTO.getEmail());
        funcionarioCriado.setSenha(funcionarioDTO.getSenha());
        funcionarioCriado.setCpf(funcionarioDTO.getCpf());
        funcionarioCriado.setUrlFoto(funcionarioDTO.getUrlFoto());
        funcionarioCriado.setEmpresa(funcionarioDTO.getEmpresa());
        funcionarioCriado.setAcesso(funcionarioDTO.getAcesso());

        return ResponseEntity.status(201).body(funcionarioService.cadastrar(funcionarioCriado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Funcionario> atualizarFuncionario(@PathVariable Long id,
                                                            @Valid @RequestBody FuncionarioRequestDTO funcionarioDTO) {
        Funcionario funcionarioExistente = funcionarioService.listarPorId(id);

        funcionarioExistente.setNome(funcionarioDTO.getNome());
        funcionarioExistente.setEmail(funcionarioDTO.getEmail());
        funcionarioExistente.setSenha(funcionarioDTO.getSenha());
        funcionarioExistente.setCpf(funcionarioDTO.getCpf());
        funcionarioExistente.setUrlFoto(funcionarioDTO.getUrlFoto());
        funcionarioExistente.setEmpresa(funcionarioDTO.getEmpresa());
        funcionarioExistente.setAcesso(funcionarioDTO.getAcesso());

        Funcionario atualizado = funcionarioService.atualizar(id, funcionarioExistente);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarFuncionario(@PathVariable Long id) {
        funcionarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}