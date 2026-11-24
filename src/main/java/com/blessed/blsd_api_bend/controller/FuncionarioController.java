package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.funcionario.FuncionarioAtualizarRequestDTO;
import com.blessed.blsd_api_bend.dto.funcionario.FuncionarioRequestDTO;
import com.blessed.blsd_api_bend.dto.funcionario.FuncionarioResponseDTO;
import com.blessed.blsd_api_bend.dto.usuario.UsuarioMapper;
import com.blessed.blsd_api_bend.dto.usuario.UsuarioDetalheDTO;
import com.blessed.blsd_api_bend.model.entity.Funcionario;
import com.blessed.blsd_api_bend.service.FuncionarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
@RestController
@RequestMapping("/funcionario")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('GESTOR', 'FUNCIONARIO')")
    public ResponseEntity<Page<FuncionarioResponseDTO>> listarFuncionarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sort) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(sort), "id")
        );

        return ResponseEntity.ok(funcionarioService.listarTodos(pageable));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FuncionarioResponseDTO> buscarMeuPerfil(
            @AuthenticationPrincipal UsuarioDetalheDTO usuarioAutenticado) {
        Funcionario funcionario = funcionarioService.listarPorId(usuarioAutenticado.getId());
        return ResponseEntity.ok(UsuarioMapper.toResponseDTO(funcionario));
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FuncionarioResponseDTO> atualizarMeuPerfil(
            @AuthenticationPrincipal UsuarioDetalheDTO usuarioAutenticado,
            @Valid @RequestBody FuncionarioAtualizarRequestDTO funcionarioDTO) {
        Funcionario atualizado = funcionarioService.atualizar(
                usuarioAutenticado.getId(),
                funcionarioDTO,
                usuarioAutenticado
        );
        return ResponseEntity.ok(UsuarioMapper.toResponseDTO(atualizado));
    }

    @PatchMapping("/me/foto")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> atualizarMinhaFoto(
            @AuthenticationPrincipal UsuarioDetalheDTO usuarioAutenticado,
            @RequestParam("file") MultipartFile file) {
        funcionarioService.atualizarFotoPerfil(
                usuarioAutenticado.getId(),
                file,
                usuarioAutenticado
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GESTOR') or (hasAuthority('FUNCIONARIO') and #id == principal.id)")
    public ResponseEntity<FuncionarioResponseDTO> listarPorId(@PathVariable Long id) {
        Funcionario funcionario = funcionarioService.listarPorId(id);
        return ResponseEntity.ok(UsuarioMapper.toResponseDTO(funcionario));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('GESTOR')")
    public ResponseEntity<FuncionarioResponseDTO> criarFuncionario(@Valid @RequestBody FuncionarioRequestDTO funcionarioRequestDTO) {
        FuncionarioResponseDTO salvo = funcionarioService.cadastrar(funcionarioRequestDTO);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();

        return ResponseEntity.created(location).body(salvo);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('GESTOR')")
    public ResponseEntity<FuncionarioResponseDTO> atualizarFuncionario(
            @PathVariable Long id,
            @Valid @RequestBody FuncionarioAtualizarRequestDTO funcionarioDTO) {

        Funcionario atualizado = funcionarioService.atualizar(id, funcionarioDTO);
        return ResponseEntity.ok(UsuarioMapper.toResponseDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('GESTOR')")
    public ResponseEntity<Void> deletarFuncionario(@PathVariable Long id) {
        funcionarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/foto")
    @PreAuthorize("hasAuthority('GESTOR')")
    public ResponseEntity<Void> atualizarFoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        funcionarioService.atualizarFotoPerfil(id, file);
        return ResponseEntity.noContent().build();
    }
}