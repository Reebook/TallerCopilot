package com.tallercopilot.conciliacion.application.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ConciliacionPayload(
        @NotNull @Valid Metadata metadata,
        @NotNull @Valid Ejecucion ejecucion,
        @NotNull @Valid Contexto contexto,
        @NotEmpty List<@Valid CuentaData> cuentas,
        @NotNull @Valid ControlTotales controlTotales,
        @NotNull List<@Valid Incidente> incidentes
) {
}
