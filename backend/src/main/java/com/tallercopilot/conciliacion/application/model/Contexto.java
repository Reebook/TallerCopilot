package com.tallercopilot.conciliacion.application.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Contexto(
        @NotBlank String banco,
        @NotBlank String sucursal,
        @NotBlank String moneda,
        @NotNull @Valid Periodo periodo,
        @NotNull @Valid RangoCuentas rangoCuentas
) {
}
