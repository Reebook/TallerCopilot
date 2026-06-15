package com.tallercopilot.conciliacion.application;

import java.math.BigDecimal;

public record DashboardSummary(
        int totalCuentasProcesadas,
        int totalCuentasConciliadas,
        int totalCuentasConDiferencia,
        BigDecimal sumatoriaSaldoFinalFuente,
        BigDecimal sumatoriaSaldoFinalConciliado,
        BigDecimal sumatoriaDiferenciaNeta
) {
}
