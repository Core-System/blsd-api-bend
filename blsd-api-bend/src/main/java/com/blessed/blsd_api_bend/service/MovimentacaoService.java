package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.dto.produto.MovimentacaoRequestDTO;
import com.blessed.blsd_api_bend.model.entity.MovimentacaoATT;
import com.blessed.blsd_api_bend.model.entity.Produto;
import com.blessed.blsd_api_bend.repository.MovimentacaoRepositoryATT;
import com.blessed.blsd_api_bend.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepositoryATT movimentacaoRepository;
    private final ProdutoRepository produtoRepository;

    public MovimentacaoService(MovimentacaoRepositoryATT movimentacaoRepository,
                               ProdutoRepository produtoRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public MovimentacaoATT registrar(MovimentacaoRequestDTO dto) {
        Produto produto = produtoRepository.findById(dto.getProdutoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // Atualiza estoque automaticamente
        if (dto.getTipo() == MovimentacaoATT.TipoMovimentacao.ENTRADA) {
            produto.setQuantidade(produto.getQuantidade() + dto.getQuantidade());
        } else {
            if (produto.getQuantidade() < dto.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente");
            }
            produto.setQuantidade(produto.getQuantidade() - dto.getQuantidade());
        }
        produtoRepository.save(produto);

        MovimentacaoATT mov = new MovimentacaoATT();
        mov.setProduto(produto);
        mov.setTipo(dto.getTipo());
        mov.setQuantidade(dto.getQuantidade());
        mov.setObservacao(dto.getObservacao());
        mov.setDataHora(LocalDateTime.now());

        return movimentacaoRepository.save(mov);
    }

    public List<MovimentacaoATT> listarUltimas() {
        return movimentacaoRepository.findTop10ByOrderByDataHoraDesc();
    }
}