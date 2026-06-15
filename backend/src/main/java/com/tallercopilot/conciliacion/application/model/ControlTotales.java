package com.tallercopilot.conciliacion.application.model;

import java.math.BigDecimal;

public record ControlTotales(
        int totalCuentasProcesadas,
        int totalCuentasConciliadas,
        int totalCuentasConDiferencia,
        BigDecimal sumatoriaSaldoFinalFuente,
        BigDecimal sumatoriaSaldoFinalConciliado,
        BigDecimal sumatoriaDiferenciaNeta
) {
}
