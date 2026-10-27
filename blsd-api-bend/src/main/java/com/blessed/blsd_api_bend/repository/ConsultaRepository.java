package com.blessed.blsd_api_bend.repository;

import com.blessed.blsd_api_bend.model.entity.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    List<Consulta> findByClienteId(Long clienteId);

    @Query("SELECT c FROM Consulta c WHERE c.avaliacao IS NOT NULL")
    List<Consulta> findConsultasAvaliadas();

    @Query("SELECT c FROM Consulta c WHERE c.statusConsulta != 'CANCELADA' AND " +
            "((c.dataHoraInicio <= :fim) AND (c.dataHoraFim >= :inicio))")
    List<Consulta> findConsultasConflitantes(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}
