package com.tallercopilot.conciliacion.application.model;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record PartidaConciliatoria(
        @NotBlank String idPartida,
        @NotBlank String tipo,
        @NotBlank String estado,
        @NotBlank String fechaPartida,
        BigDecimal monto
) {
}
