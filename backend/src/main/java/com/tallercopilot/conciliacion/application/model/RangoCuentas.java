package com.tallercopilot.conciliacion.application.model;

import jakarta.validation.constraints.NotBlank;

public record RangoCuentas(
        @NotBlank String desde,
        @NotBlank String hasta
) {
}
