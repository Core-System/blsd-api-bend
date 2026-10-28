package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.dto.dashboard.ProximoAgendamentoDTO;
import com.blessed.blsd_api_bend.dto.dashboard.ReceitaAnualDTO;
import com.blessed.blsd_api_bend.dto.dashboard.ServicoRankingDTO;
import com.blessed.blsd_api_bend.dto.dashboard.TendenciaFaturamentoDTO;
import com.blessed.blsd_api_bend.repository.DashboardRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    public DashboardService(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

        // ── KPIs ──
    public BigDecimal getFaturamentoMesAtual() {
    return dashboardRepository.calcularFaturamentoMesAtual();
    }

    public Long getProcedimento(){
        return dashboardRepository.contarProcedimentosMesAtual();
    }

    public BigDecimal getReceitaAnual() {
        return dashboardRepository.calcularReceitaAnual();
    }

    public BigDecimal getTicketMedio() {
        return dashboardRepository.calcularTicketMedio();
    }

        // ── Gráfico de linha — últimos 6 meses ──
        public List<TendenciaFaturamentoDTO> getTendenciaFaturamento() {
        LocalDateTime primeiraConsulta = dashboardRepository.buscarDataPrimeiraConsulta();
        LocalDateTime dataInicio = primeiraConsulta != null ? primeiraConsulta : LocalDateTime.now();

        List<TendenciaFaturamentoDTO> tendencia = dashboardRepository.calcularTendenciaFaturamento(dataInicio);
        return tendencia;
    }

        // ── Gráfico de barras — últimos 5 anos ──
        public List<ReceitaAnualDTO> getReceitaAtual() {
        return dashboardRepository.calcularReceitaPorAno();
    }

        // ── Ranking de serviços ──
        public List<ServicoRankingDTO> getServicoRanking() {
        return dashboardRepository.listarServicosRanking();
    }

        // ── Próximos agendamentos (top 5) ──
        public List<ProximoAgendamentoDTO> getProximosAgendamentos(){
        return dashboardRepository.listarProximosAgendamentos();
    }
}