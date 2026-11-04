package com.blessed.blsd_api_bend.repository;

import com.blessed.blsd_api_bend.model.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProdutoRepository extends JpaRepository<Produto,Long> {

    Boolean existsByNome(String nome);

    String findByNome(String nome);

    @Query("""
            SELECT p FROM Produto p
            WHERE :busca IS NULL
               OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
            """)
    Page<Produto> buscar(@Param("busca") String busca, Pageable pageable);
}
