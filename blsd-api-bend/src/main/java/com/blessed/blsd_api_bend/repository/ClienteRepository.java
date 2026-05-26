package com.blessed.blsd_api_bend.repository;

import com.blessed.blsd_api_bend.model.entity.Cliente;
import com.blessed.blsd_api_bend.model.entity.ClienteAgendamento;
import com.blessed.blsd_api_bend.model.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente,Long> {


    Boolean existsByEmail(String email);

    Optional<Cliente> findByEmail(String email);


}
