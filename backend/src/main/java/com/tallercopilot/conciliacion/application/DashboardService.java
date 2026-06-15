package com.tallercopilot.conciliacion.application;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final ConciliacionDataService conciliacionDataService;

    public DashboardService(ConciliacionDataService conciliacionDataService) {
        this.conciliacionDataService = conciliacionDataService;
    }

    public DashboardSummary getSummary() {
        var controlTotales = conciliacionDataService.getPayload().controlTotales();
        return new DashboardSummary(
                controlTotales.totalCuentasProcesadas(),
                controlTotales.totalCuentasConciliadas(),
                controlTotales.totalCuentasConDiferencia(),
                valueOrZero(controlTotales.sumatoriaSaldoFinalFuente()),
                valueOrZero(controlTotales.sumatoriaSaldoFinalConciliado()),
                valueOrZero(controlTotales.sumatoriaDiferenciaNeta())
        );
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
