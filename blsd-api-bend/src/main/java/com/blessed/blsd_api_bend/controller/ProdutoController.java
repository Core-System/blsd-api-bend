package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.produto.ProdutoRequestDTO;
import com.blessed.blsd_api_bend.dto.produto.ProdutoResponseDTO;
import com.blessed.blsd_api_bend.model.entity.Produto;
import com.blessed.blsd_api_bend.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produto")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    private ProdutoResponseDTO toDTO(Produto p) {
        return new ProdutoResponseDTO(p.getId(), p.getNome(), p.getPreco(), p.getQuantidade());
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarProdutos() {
        List<ProdutoResponseDTO> dtos = produtoService.listarTodos()
                .stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> listarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toDTO(produtoService.listarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criarProduto(@Valid @RequestBody ProdutoRequestDTO produtoDTO) {
        Produto produtoCriado = new Produto();
        produtoCriado.setNome(produtoDTO.getNome());
        produtoCriado.setPreco(produtoDTO.getPreco());
        produtoCriado.setQuantidade(produtoDTO.getQuantidade());

        return ResponseEntity.status(201).body(toDTO(produtoService.cadastrar(produtoCriado)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizarProduto(@PathVariable Long id,
                                                               @Valid @RequestBody ProdutoRequestDTO produtoDTO) {
        Produto produtoExistente = produtoService.listarPorId(id);
        produtoExistente.setNome(produtoDTO.getNome());
        produtoExistente.setPreco(produtoDTO.getPreco());
        produtoExistente.setQuantidade(produtoDTO.getQuantidade());

        return ResponseEntity.ok(toDTO(produtoService.atualizar(id, produtoExistente)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}