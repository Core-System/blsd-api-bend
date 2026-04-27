package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.cliente.ClienteRequestDTO;
import com.blessed.blsd_api_bend.model.entity.Cliente;
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


    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }


    @GetMapping
    public ResponseEntity<List<Cliente>> getCliente() {
        List<Cliente> clientes = clienteService.listarClientes();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Long id) {
        Cliente Cliente = clienteService.listarPorId(id);
        return ResponseEntity.ok(Cliente);
    }

    @PostMapping
    public ResponseEntity<Cliente> criarCliente(@Valid @RequestBody ClienteRequestDTO cliente) {
        Cliente clienteCriado = new Cliente();

        clienteCriado.setNome(cliente.getNome());
        clienteCriado.setEmail(cliente.getEmail());
        clienteCriado.setSenha(cliente.getSenha());
        clienteCriado.setDataNasc(cliente.getDataNasc());
        clienteCriado.setUrlFoto(cliente.getUrlFoto());
        clienteCriado.setTelefone(cliente.getTelefone());
        clienteCriado.setDataCriacao(LocalDateTime.now());


        return ResponseEntity.status(201).body(clienteService.cadastrarCliente(clienteCriado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizarCliente(@PathVariable Long id,
                                                    @Valid @RequestBody ClienteRequestDTO clienteDTO) {
        Cliente clienteExistente = clienteService.listarPorId(id);


        clienteExistente.setNome(clienteDTO.getNome());
        clienteExistente.setEmail(clienteDTO.getEmail());
        clienteExistente.setSenha(clienteDTO.getSenha());
        clienteExistente.setDataNasc(clienteDTO.getDataNasc());
        clienteExistente.setUrlFoto(clienteDTO.getUrlFoto());
        clienteExistente.setTelefone(clienteDTO.getTelefone());


        Cliente atualizado = clienteService.atualizarCliente(id,clienteExistente);

        return ResponseEntity.ok(atualizado)    ;
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        clienteService.deletarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody ClienteRequestDTO cliente) {
        Cliente existente = clienteService.buscarPorEmail(cliente.getEmail());
        if (existente != null && existente.getSenha().equals(cliente.getSenha())) {
            return ResponseEntity.ok("Login realizado com sucesso");
        }
        return ResponseEntity.status(401).body("Credenciais inválidas");
    }


}
