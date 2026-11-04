package com.blessed.blsd_api_bend.repository;

import com.blessed.blsd_api_bend.model.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.blessed.blsd_api_bend.model.enums.TiposAcessos;

import java.util.Optional;
import java.util.List;

public interface FuncionarioRepository extends JpaRepository<Funcionario,Long> {

    Boolean existsByEmail(String email);

    Boolean existsByCpf(String cpf);

    Optional<Funcionario> findByEmail(String email);

    Page<Funcionario> findByAcessoNomeNot(TiposAcessos acesso, Pageable pageable);

    List<Funcionario> findByAcessoNomeNot(TiposAcessos acesso);

}
