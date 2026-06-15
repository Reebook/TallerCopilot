package com.tallercopilot.conciliacion.application.model;

import jakarta.validation.constraints.NotBlank;

public record Ejecucion(
        @NotBlank String idEjecucion,
        @NotBlank String fechaProceso,
        @NotBlank String fechaHoraInicio,
        @NotBlank String fechaHoraFin,
        @NotBlank String usuario,
        @NotBlank String programa,
        @NotBlank String libreria,
        @NotBlank String estadoEjecucion
) {
}
