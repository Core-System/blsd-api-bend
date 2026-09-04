package com.blessed.blsd_api_bend.repository;

import com.blessed.blsd_api_bend.model.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente,Long> {


    Boolean existsByEmail(String email);

    Boolean existsByTelefone(String telefone);

    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findByTokenAgendamento(String token);
}
