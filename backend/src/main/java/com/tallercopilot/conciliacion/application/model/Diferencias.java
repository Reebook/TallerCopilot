package com.tallercopilot.conciliacion.application.model;

import java.math.BigDecimal;

public record Diferencias(
        BigDecimal diferenciaNeta,
        BigDecimal diferenciaAbsoluta,
        BigDecimal toleranciaPermitida,
        boolean excedeTolerancia,
        String motivoPrincipal
) {
}
