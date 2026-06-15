package com.tallercopilot.conciliacion.application.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CuentaData(
        @NotNull @Valid CuentaMayor cuentaMayor,
        @NotNull @Valid Saldos saldos,
        @NotNull @Valid Diferencias diferencias,
        @NotNull @Valid EstadoConciliacion estadoConciliacion,
        @NotNull List<@Valid PartidaConciliatoria> partidasConciliatorias
) {
}
