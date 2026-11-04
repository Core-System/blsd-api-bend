package com.blessed.blsd_api_bend.repository;

import com.blessed.blsd_api_bend.model.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente,Long> {


    Boolean existsByEmail(String email);

    Boolean existsByTelefone(String telefone);

    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findByTokenAgendamento(String token);

    @Query("""
            SELECT c FROM Cliente c
            WHERE :busca IS NULL
               OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(c.email) LIKE LOWER(CONCAT('%', :busca, '%'))
            """)
    Page<Cliente> buscar(@Param("busca") String busca, Pageable pageable);
}
