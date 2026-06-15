package com.tallercopilot.conciliacion.application.model;

import jakarta.validation.constraints.NotBlank;

public record EstadoConciliacion(
        @NotBlank String codigo,
        @NotBlank String descripcion,
        @NotBlank String severidad,
        boolean requiereRevision
) {
}
