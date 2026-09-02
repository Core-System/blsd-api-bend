package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.cliente.ClienteAtualizarRequestDTO;
import com.blessed.blsd_api_bend.dto.cliente.ClienteRequestDTO;
import com.blessed.blsd_api_bend.dto.cliente.ClienteResponseDTO;
import com.blessed.blsd_api_bend.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('GESTOR', 'FUNCIONARIO')")
    public ResponseEntity<Page<ClienteResponseDTO>> listarClientes(Pageable pageable) {
        return ResponseEntity.ok(clienteService.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GESTOR') or (hasAuthority('CLIENTE') and #id == principal.id)")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> criarCliente(@Valid @RequestBody ClienteRequestDTO clienteDTO) {
        ClienteResponseDTO salvo = clienteService.cadastrar(clienteDTO);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();

        return ResponseEntity.created(location).body(salvo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('GESTOR') or (hasAuthority('CLIENTE') and #id == principal.id)")
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(@PathVariable Long id,
                                                               @Valid @RequestBody ClienteAtualizarRequestDTO clienteDTO) {
        return ResponseEntity.ok(clienteService.atualizar(id, clienteDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('GESTOR', 'FUNCIONARIO')")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/foto")
    public ResponseEntity<Void> atualizarFoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        clienteService.atualizarFotoPerfil(id, file);
        return ResponseEntity.noContent().build();
    }
}