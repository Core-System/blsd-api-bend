package com.blessed.blsd_api_bend.repository;

import com.blessed.blsd_api_bend.dto.dashboard.ProximoAgendamentoDTO;
import com.blessed.blsd_api_bend.dto.dashboard.ReceitaAnualDTO;
import com.blessed.blsd_api_bend.dto.dashboard.ServicoRankingDTO;
import com.blessed.blsd_api_bend.dto.dashboard.TendenciaFaturamentoDTO;
import com.blessed.blsd_api_bend.model.entity.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DashboardRepository extends JpaRepository<Consulta, Long> {

    @Query("""
        SELECT COALESCE(SUM(s.preco), 0)
        FROM Consulta c
        JOIN c.servicos s
        WHERE MONTH(c.dataHoraInicio) = MONTH(CURRENT_DATE)
          AND YEAR(c.dataHoraInicio)  = YEAR(CURRENT_DATE)
    """)
    BigDecimal calcularFaturamentoMesAtual();

//    @Query("""
//        SELECT COALESCE(SUM(s.preco), 0)
//        FROM Consulta c
//        JOIN c.servicos s
//        WHERE MONTH(c.dataHoraInicio) = MONTH(CURRENT_DATE) - 1
//          AND YEAR(c.dataHoraInicio)  = YEAR(CURRENT_DATE)
//    """)
//    BigDecimal calcularFaturamentoMesAnterior();

    @Query("""
        SELECT COUNT(c)
        FROM Consulta c
        WHERE MONTH(c.dataHoraInicio) = MONTH(CURRENT_DATE)
          AND YEAR(c.dataHoraInicio)  = YEAR(CURRENT_DATE)
    """)
    Long contarProcedimentosMesAtual();

//    @Query("""
//        SELECT COUNT(c)
//        FROM Consulta c
//        WHERE MONTH(c.dataHoraInicio) = MONTH(CURRENT_DATE) - 1
//          AND YEAR(c.dataHoraInicio)  = YEAR(CURRENT_DATE)
//    """)
//    Long contarProcedimentosMesAnterior();

    @Query("""
        SELECT COALESCE(SUM(s.preco), 0)
        FROM Consulta c
        JOIN c.servicos s
        WHERE YEAR(c.dataHoraInicio) = YEAR(CURRENT_DATE)
    """)
    BigDecimal calcularReceitaAnual();

    @Query(value = """
        SELECT COALESCE(AVG(sub.total), 0)
        FROM (
            SELECT SUM(s.preco) AS total
            FROM consulta c
            JOIN consulta_servico cs ON cs.consulta_id = c.id
            JOIN servico s ON s.id = cs.servico_id
            WHERE MONTH(c.data_hora_inicio) = MONTH(CURRENT_DATE)
              AND YEAR(c.data_hora_inicio)  = YEAR(CURRENT_DATE)
            GROUP BY c.id
        ) sub
    """, nativeQuery = true)
    BigDecimal calcularTicketMedio();

    @Query("""
        SELECT new com.blessed.blsd_api_bend.dto.dashboard.TendenciaFaturamentoDTO(
               YEAR(c.dataHoraInicio),
               MONTH(c.dataHoraInicio),
               SUM(s.preco))
        FROM Consulta c
        JOIN c.servicos s
        WHERE c.dataHoraInicio >= :dataInicio
        GROUP BY YEAR(c.dataHoraInicio), MONTH(c.dataHoraInicio)
        ORDER BY YEAR(c.dataHoraInicio) ASC, MONTH(c.dataHoraInicio) ASC
    """)
    List<TendenciaFaturamentoDTO> calcularTendenciaFaturamento(@Param("dataInicio") LocalDateTime dataInicio);

    @Query("""
    SELECT MIN(c.dataHoraInicio)
    FROM Consulta c
""")
    LocalDateTime buscarDataPrimeiraConsulta();

    @Query("""
        SELECT new com.blessed.blsd_api_bend.dto.dashboard.ReceitaAnualDTO(
               YEAR(c.dataHoraInicio),
               SUM(s.preco))
        FROM Consulta c
        JOIN c.servicos s
        WHERE YEAR(c.dataHoraInicio) >= YEAR(CURRENT_DATE) - 4
        GROUP BY YEAR(c.dataHoraInicio)
        ORDER BY YEAR(c.dataHoraInicio) ASC
    """)
    List<ReceitaAnualDTO> calcularReceitaPorAno();

    @Query("""
        SELECT new com.blessed.blsd_api_bend.dto.dashboard.ServicoRankingDTO(
               s.nome,
               COUNT(s.id))
        FROM Consulta c
        JOIN c.servicos s
        GROUP BY s.id, s.nome
        ORDER BY COUNT(s.id) DESC
    """)
    List<ServicoRankingDTO> listarServicosRanking();

    @Query("""
        SELECT new com.blessed.blsd_api_bend.dto.dashboard.ProximoAgendamentoDTO(
               c.id,
               c.dataHoraInicio,
               c.dataHoraFim,
               cl.nome,
               cl.urlFoto,
               c.localConsulta,
               c.tipoPagamento)
        FROM Consulta c
        JOIN c.cliente cl
        WHERE c.dataHoraInicio >= CURRENT TIMESTAMP
        ORDER BY c.dataHoraInicio ASC
    """)
    List<ProximoAgendamentoDTO> listarProximosAgendamentos();
}