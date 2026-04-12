package com.blessed.blsd_api_bend.repository;

import com.blessed.blsd_api_bend.model.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente,Long> {

    Boolean existsByEmail(String email);

}
