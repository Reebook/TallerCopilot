package com.tallercopilot.conciliacion.application.model;

import jakarta.validation.constraints.NotBlank;

public record Incidente(
        @NotBlank String codigo,
        @NotBlank String tipo,
        @NotBlank String cuentaContable,
        @NotBlank String mensaje,
        @NotBlank String severidad
) {
}
