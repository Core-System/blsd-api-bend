package com.blessed.blsd_api_bend.controller;

import com.blessed.blsd_api_bend.dto.dashboard.ProximoAgendamentoDTO;
import com.blessed.blsd_api_bend.dto.dashboard.ReceitaAnualDTO;
import com.blessed.blsd_api_bend.dto.dashboard.ServicoRankingDTO;
import com.blessed.blsd_api_bend.dto.dashboard.TendenciaFaturamentoDTO;
import com.blessed.blsd_api_bend.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // ── KPI: Faturamento Mês Atual ──
    @GetMapping("/faturamento-mensal")
    public ResponseEntity<BigDecimal> getFaturamentoMes() {
        BigDecimal faturamentoMensal = dashboardService.getFaturamentoMesAtual();
        if (faturamentoMensal == null) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.ok(faturamentoMensal);
    }

    // ── KPI: Quantidade de Procedimentos Mês Atual ──
    @GetMapping("/procedimentos-mensal")
    public ResponseEntity<Long> getProcedimento() {
        Long totalProcedimentos = dashboardService.getProcedimento();
        if (totalProcedimentos == null) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.ok(totalProcedimentos);
    }

    // ── KPI: Receita Anual Total ──
    @GetMapping("/receita-anual-total")
    public ResponseEntity<BigDecimal> getReceitaAnual() {
        BigDecimal receitaAnual = dashboardService.getReceitaAnual();
        if (receitaAnual == null) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.ok(receitaAnual);
    }

    // ── KPI: Ticket Médio ──
    @GetMapping("/ticket-medio")
    public ResponseEntity<BigDecimal> getTicketMedio() {
        BigDecimal ticketMedio = dashboardService.getTicketMedio();
        if (ticketMedio == null) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.ok(ticketMedio);
    }

    // ── Gráfico de linha: Tendência de Faturamento (Últimos 6 meses) ──
    @GetMapping("/tendencia-faturamento")
    public ResponseEntity<List<TendenciaFaturamentoDTO>> getTendenciaFaturamento() {
        List<TendenciaFaturamentoDTO> tendencia = dashboardService.getTendenciaFaturamento();
        if (tendencia == null || tendencia.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.ok(tendencia);
    }

    // ── Gráfico de barras: Receita por Ano (Últimos 5 anos) ──
    @GetMapping("/receita-por-ano")
    public ResponseEntity<List<ReceitaAnualDTO>> getReceitaAtual() {
        List<ReceitaAnualDTO> receitaAnual = dashboardService.getReceitaAtual();
        if (receitaAnual == null || receitaAnual.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.ok(receitaAnual);
    }

    // ── Ranking de Serviços ──
    @GetMapping("/ranking-servicos")
    public ResponseEntity<List<ServicoRankingDTO>> getServicoRanking() {
        List<ServicoRankingDTO> ranking = dashboardService.getServicoRanking();
        if (ranking == null || ranking.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.ok(ranking);
    }

    // ── Próximos Agendamentos (Top 5) ──
    @GetMapping("/proximos-agendamentos")
    public ResponseEntity<List<ProximoAgendamentoDTO>> getProximosAgendamentos() {
        List<ProximoAgendamentoDTO> proximos = dashboardService.getProximosAgendamentos();
        if (proximos == null || proximos.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.ok(proximos);
    }
}