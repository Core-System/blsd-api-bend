package com.blessed.blsd_api_bend.repository;

import com.blessed.blsd_api_bend.model.entity.Acesso;
import com.blessed.blsd_api_bend.model.enums.TiposAcessos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcessoRepository extends JpaRepository<Acesso, Long> {
    Optional<Acesso> findByNome(TiposAcessos nome);
}