package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.produto.ProdutoRequestDTO;
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

    @GetMapping
    public ResponseEntity<List<Produto>> listarProdutos() {
        List<Produto> produtos = produtoService.listarTodos();
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> listarPorId(@PathVariable Long id) {
        Produto produto = produtoService.listarPorId(id);
        return ResponseEntity.ok(produto);
    }

    @PostMapping
    public ResponseEntity<Produto> criarProduto(@Valid @RequestBody ProdutoRequestDTO produtoDTO) {
        Produto produtoCriado = new Produto();

        produtoCriado.setNome(produtoDTO.getNome());
        produtoCriado.setPreco(produtoDTO.getPreco());
        produtoCriado.setServico(produtoDTO.getServico());
        produtoCriado.setQuantidade(produtoDTO.getQuantidade());

        return ResponseEntity.status(201).body(produtoService.cadastrar(produtoCriado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizarProduto(@PathVariable Long id,
                                                            @Valid @RequestBody ProdutoRequestDTO produtoDTO) {
        Produto produtoExistente = produtoService.listarPorId(id);

        produtoExistente.setNome(produtoDTO.getNome());
        produtoExistente.setPreco(produtoDTO.getPreco());
        produtoExistente.setServico(produtoDTO.getServico());
        produtoExistente.setQuantidade(produtoDTO.getQuantidade());

        Produto atualizado = produtoService.atualizar(id, produtoExistente);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
