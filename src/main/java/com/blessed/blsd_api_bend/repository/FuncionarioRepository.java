package com.blessed.blsd_api_bend.repository;

import com.blessed.blsd_api_bend.model.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario,Long> {

    Boolean existsByEmail(String email);

    Boolean existsByCpf(String cpf);

    Optional<Funcionario> findByEmail(String email);


}
