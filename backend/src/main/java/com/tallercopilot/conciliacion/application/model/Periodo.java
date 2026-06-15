package com.tallercopilot.conciliacion.application.model;

import jakarta.validation.constraints.NotBlank;

public record Periodo(
        int anio,
        int mes,
        @NotBlank String fechaCorte
) {
}
