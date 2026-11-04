package com.blessed.blsd_api_bend.repository;

import com.blessed.blsd_api_bend.model.entity.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    List<Consulta> findByClienteId(Long clienteId);

    @Query("""
            SELECT DISTINCT c FROM Consulta c
            JOIN c.cliente cliente
            LEFT JOIN c.servicos servico
            WHERE (:status IS NULL OR c.statusConsulta = :status)
              AND (:busca IS NULL OR LOWER(cliente.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
                   OR LOWER(cliente.email) LIKE LOWER(CONCAT('%', :busca, '%')))
              AND (:procedimento IS NULL OR LOWER(servico.nome) LIKE LOWER(CONCAT('%', :procedimento, '%')))
              AND (:inicio IS NULL OR c.dataHoraInicio >= :inicio)
              AND (:fim IS NULL OR c.dataHoraInicio < :fim)
            """)
    Page<Consulta> buscar(@Param("status") com.blessed.blsd_api_bend.model.enums.StatusConsulta status,
                          @Param("busca") String busca,
                          @Param("procedimento") String procedimento,
                          @Param("inicio") LocalDateTime inicio,
                          @Param("fim") LocalDateTime fim,
                          Pageable pageable);

    @Query("SELECT c FROM Consulta c WHERE c.avaliacao IS NOT NULL")
    List<Consulta> findConsultasAvaliadas();

    @Query("SELECT c FROM Consulta c WHERE c.statusConsulta != 'CANCELADA' AND " +
            "((c.dataHoraInicio <= :fim) AND (c.dataHoraFim >= :inicio))")
    List<Consulta> findConsultasConflitantes(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}
