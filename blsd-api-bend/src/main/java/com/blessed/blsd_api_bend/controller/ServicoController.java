package com.blessed.blsd_api_bend.controller;


import com.blessed.blsd_api_bend.dto.servico.ServicoRequestDTO;
import com.blessed.blsd_api_bend.model.entity.Servico;
import com.blessed.blsd_api_bend.service.ServicoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servico")
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }



    @GetMapping
    public ResponseEntity<List<Servico>> listarServicos() {
        List<Servico> servicos = servicoService.listarTodos();
        return ResponseEntity.ok(servicos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Servico> listarPorId(@PathVariable Long id) {
        Servico servico = servicoService.listarPorId(id);
        return ResponseEntity.ok(servico);
    }

    @PostMapping
    public ResponseEntity<Servico> criarServico(@Valid @RequestBody ServicoRequestDTO servicoDTO) {
        Servico servicoCriado = new Servico();

        servicoCriado.setNome(servicoDTO.getNome());
        servicoCriado.setPreco(servicoDTO.getPreco());
        servicoCriado.setAvaliacao(servicoDTO.getAvaliacao());
        servicoCriado.setDuracao(servicoDTO.getDuracao());
        servicoCriado.setDescricacao(servicoDTO.getDescricacao());
        servicoCriado.setProdutos(servicoDTO.getProduto());


        return ResponseEntity.status(201).body(servicoService.cadastrar(servicoCriado));
    }

/*    @PutMapping("/{id}")
    public ResponseEntity<Servico> atualizarServico(@PathVariable Long id,
                                                    @Valid @RequestBody ServicoRequestDTO servicoDTO) {
        Servico servicoExistente = servicoService.listarPorId(id);


        servicoExistente.setNome(servicoDTO.getNome());
        servicoExistente.setPreco(servicoDTO.getPreco());
        servicoExistente.setAvaliacao(servicoDTO.getAvaliacao());
        servicoExistente.setDuracao(servicoDTO.getDuracao());
        servicoExistente.setDescricacao(servicoDTO.getDescricacao());
        servicoExistente.setProdutos(servicoDTO.getProduto());

        Servico atualizado = servicoService.atualizar(id, servicoExistente);
        return ResponseEntity.ok(atualizado);
    }
*/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarServico(@PathVariable Long id) {
        servicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
