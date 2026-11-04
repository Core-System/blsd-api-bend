package com.blessed.blsd_api_bend.repository;

import com.blessed.blsd_api_bend.model.entity.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ServicoRepository extends JpaRepository<Servico,Long> {

    Boolean existsByNome(String nome);

    Optional<Servico> findByNome(String nome);

    @Query("""
            SELECT s FROM Servico s
            WHERE (:busca IS NULL OR LOWER(s.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(s.descricao) LIKE LOWER(CONCAT('%', :busca, '%')))
              AND (:categoria IS NULL OR LOWER(s.categoria) = LOWER(:categoria))
            """)
    Page<Servico> buscar(@Param("busca") String busca,
                         @Param("categoria") String categoria,
                         Pageable pageable);
}
