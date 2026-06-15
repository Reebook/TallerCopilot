package com.tallercopilot.conciliacion.application.model;

import jakarta.validation.constraints.NotBlank;

public record CuentaMayor(
        @NotBlank String codigoBanco,
        @NotBlank String codigoSucursal,
        @NotBlank String codigoMoneda,
        @NotBlank String cuentaContable,
        @NotBlank String descripcionCuenta
) {
}
