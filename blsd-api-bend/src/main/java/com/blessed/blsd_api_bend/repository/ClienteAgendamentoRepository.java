package com.blessed.blsd_api_bend.repository;

import com.blessed.blsd_api_bend.model.entity.ClienteAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteAgendamentoRepository extends JpaRepository<ClienteAgendamento, Long> {
    ClienteAgendamento findByEmail(String email);
}
