package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.exception.produto.ProdutoAlreadyExistsException;
import com.blessed.blsd_api_bend.exception.produto.ProdutoNotFoundException;
import com.blessed.blsd_api_bend.model.entity.Funcionario;
import com.blessed.blsd_api_bend.model.entity.Produto;
import com.blessed.blsd_api_bend.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService implements ICrudService<Produto> {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }


    @Override
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    @Override
    public Produto listarPorId(Long id) {
        return produtoRepository.findById(id).orElseThrow(()-> new ProdutoNotFoundException("Produto não encontrado"));
    }

    @Override
    public Produto cadastrar(Produto req) {
        if(produtoRepository.existsByNome(req.getNome())){
        throw new ProdutoAlreadyExistsException("Produto já existente");
        }

    return produtoRepository.save(req);
    }

    @Override
    public Produto atualizar(Long id, Produto req) {
        return produtoRepository.findById(id).stream().
                map(p->{
                    p.setNome(p.getNome());
                    p.setPreco(p.getPreco());
                    p.setServico(p.getServico());
                    p.setQuantidade(p.getQuantidade());
                    return produtoRepository.save(p);
                }).findAny().orElseThrow(()-> new ProdutoNotFoundException("Produto não encontrado") );
    }

    @Override
    public void deletar(Long id) {
        Produto produto = listarPorId(id);
        produtoRepository.delete(produto);
    }
}
