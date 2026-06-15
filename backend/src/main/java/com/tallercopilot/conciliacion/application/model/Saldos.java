package com.tallercopilot.conciliacion.application.model;

import java.math.BigDecimal;

public record Saldos(
        BigDecimal saldoInicial,
        BigDecimal debitosPeriodo,
        BigDecimal creditosPeriodo,
        BigDecimal saldoFinalCalculado,
        BigDecimal saldoFinalFuente,
        BigDecimal saldoFinalConciliado
) {
}
