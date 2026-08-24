package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.cliente.ClienteAtualizarRequestDTO;
import com.blessed.blsd_api_bend.dto.cliente.ClienteRequestDTO;
import com.blessed.blsd_api_bend.dto.cliente.ClienteResponseDTO;
import com.blessed.blsd_api_bend.dto.usuario.UsuarioMapper;
import com.blessed.blsd_api_bend.model.entity.Acesso;
import com.blessed.blsd_api_bend.model.entity.Cliente;
import com.blessed.blsd_api_bend.model.enums.TiposAcessos;
import com.blessed.blsd_api_bend.repository.AcessoRepository;
import com.blessed.blsd_api_bend.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteService clienteService;
    private final AcessoRepository acessoRepository;

    public ClienteController(ClienteService clienteService, AcessoRepository acessoRepository) {
        this.clienteService = clienteService;
        this.acessoRepository = acessoRepository;
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> getCliente() {
        List<ClienteResponseDTO> clientes = clienteService.listarTodos()
                .stream()
                .map(UsuarioMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> getClienteById(@PathVariable Long id) {
        Cliente cliente = clienteService.listarPorId(id);
        return ResponseEntity.ok(UsuarioMapper.toResponseDTO(cliente));
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> criarCliente(@Valid @RequestBody ClienteRequestDTO cliente) {
        Cliente clienteCriado = UsuarioMapper.of(cliente); // reaproveita o mapper já existente

        Acesso acessoCliente = acessoRepository.findByNome(TiposAcessos.CLIENTE)
                .orElseThrow(() -> new IllegalStateException("Acesso CLIENTE não cadastrado no banco"));
        clienteCriado.setAcesso(acessoCliente);

        Cliente salvo = clienteService.cadastrar(clienteCriado);
        return ResponseEntity.status(201).body(UsuarioMapper.toResponseDTO(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(@PathVariable Long id,
                                                               @Valid @RequestBody ClienteAtualizarRequestDTO clienteDTO) {
        Cliente clienteExistente = clienteService.listarPorId(id);
        clienteExistente.setNome(clienteDTO.getNome());
        clienteExistente.setEmail(clienteDTO.getEmail());
        clienteExistente.setDataNasc(clienteDTO.getDataNasc());
        clienteExistente.setUrlFoto(clienteDTO.getUrlFoto());
        clienteExistente.setTelefone(clienteDTO.getTelefone());

        Cliente atualizado = clienteService.atualizar(id, clienteExistente);
        return ResponseEntity.ok(UsuarioMapper.toResponseDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }




}
