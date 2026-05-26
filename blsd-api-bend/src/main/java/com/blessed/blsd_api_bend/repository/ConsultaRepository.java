package com.blessed.blsd_api_bend.repository;

import com.blessed.blsd_api_bend.model.entity.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    List<Consulta> findByClienteId(Long clienteId);
}