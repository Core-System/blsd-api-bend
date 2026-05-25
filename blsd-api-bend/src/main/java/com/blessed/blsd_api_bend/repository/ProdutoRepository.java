package com.blessed.blsd_api_bend.repository;

import com.blessed.blsd_api_bend.model.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto,Long> {

    Boolean existsByNome(String nome);

    String findByNome(String nome);
}
