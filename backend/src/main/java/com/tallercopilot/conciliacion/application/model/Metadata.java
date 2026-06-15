package com.tallercopilot.conciliacion.application.model;

import jakarta.validation.constraints.NotBlank;

public record Metadata(
        @NotBlank String versionEstructura,
        @NotBlank String sistemaOrigen,
        @NotBlank String proceso,
        @NotBlank String ambiente,
        @NotBlank String charset
) {
}
