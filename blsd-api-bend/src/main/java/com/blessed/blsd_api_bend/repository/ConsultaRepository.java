package com.blessed.blsd_api_bend.repository;
import com.blessed.blsd_api_bend.model.entity.Consulta;
import com.blessed.blsd_api_bend.model.entity.ConsultaAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
}
