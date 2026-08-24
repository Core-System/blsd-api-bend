package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.funcionario.FuncionarioAtualizarRequestDTO;
import com.blessed.blsd_api_bend.dto.funcionario.FuncionarioRequestDTO;
import com.blessed.blsd_api_bend.dto.funcionario.FuncionarioResponseDTO;
import com.blessed.blsd_api_bend.dto.usuario.UsuarioMapper;
import com.blessed.blsd_api_bend.model.entity.Acesso;
import com.blessed.blsd_api_bend.model.entity.Funcionario;
import com.blessed.blsd_api_bend.model.enums.TiposAcessos;
import com.blessed.blsd_api_bend.repository.AcessoRepository;
import com.blessed.blsd_api_bend.service.FuncionarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcionario")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;
    private final AcessoRepository acessoRepository;

    public FuncionarioController(FuncionarioService funcionarioService, AcessoRepository acessoRepository) {
        this.funcionarioService = funcionarioService;
        this.acessoRepository = acessoRepository;
    }

    @GetMapping
    public ResponseEntity<List<FuncionarioResponseDTO>> listarFuncionarios() {
        List<FuncionarioResponseDTO> funcionarios = funcionarioService.listarTodos()
                .stream()
                .map(UsuarioMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDTO> listarPorId(@PathVariable Long id) {
        Funcionario funcionario = funcionarioService.listarPorId(id);
        return ResponseEntity.ok(UsuarioMapper.toResponseDTO(funcionario));
    }

    @PostMapping
    public ResponseEntity<FuncionarioResponseDTO> criarFuncionario(@Valid @RequestBody FuncionarioRequestDTO funcionarioDTO) {
        Funcionario funcionarioCriado = UsuarioMapper.of(funcionarioDTO);

        Acesso acessoFuncionario = acessoRepository.findByNome(TiposAcessos.FUNCIONARIO)
                .orElseThrow(()-> new IllegalStateException("Acesso FUNCIONARIO não cadastrado no banco"));

        funcionarioCriado.setAcesso(acessoFuncionario);

        Funcionario salvo = funcionarioService.cadastrar(funcionarioCriado);
        return ResponseEntity.status(201).body(UsuarioMapper.toResponseDTO(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDTO> atualizarFuncionario(@PathVariable Long id,
                                                                       @Valid @RequestBody FuncionarioAtualizarRequestDTO funcionarioDTO) {
        Funcionario funcionarioExistente = funcionarioService.listarPorId(id);

        funcionarioExistente.setNome(funcionarioDTO.getNome());
        funcionarioExistente.setEmail(funcionarioDTO.getEmail());
        funcionarioExistente.setSenha(funcionarioDTO.getSenha());
        funcionarioExistente.setCpf(funcionarioDTO.getCpf());
        funcionarioExistente.setUrlFoto(funcionarioDTO.getUrlFoto());
        funcionarioExistente.setEmpresa(funcionarioDTO.getEmpresa());

        Funcionario atualizado = funcionarioService.atualizar(id, funcionarioExistente);
        return ResponseEntity.ok(UsuarioMapper.toResponseDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarFuncionario(@PathVariable Long id) {
        funcionarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}