package com.blessed.blsd_api_bend.repository;

import com.blessed.blsd_api_bend.model.entity.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServicoRepository extends JpaRepository<Servico,Long> {

    Boolean existsByNome(String nome);

    Optional<Servico> findByNome(String nome);
}
