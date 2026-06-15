package com.tallercopilot.conciliacion.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tallercopilot.conciliacion.application.model.*;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ConciliacionDataService conciliacionDataService;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        ControlTotales control = new ControlTotales(
                150, 134, 16,
                new BigDecimal("12455000.25"),
                new BigDecimal("12454980.75"),
                new BigDecimal("19.50")
        );

        ConciliacionPayload payload = new ConciliacionPayload(
                null, null, null, List.of(), control, List.of()
        );

        when(conciliacionDataService.getPayload()).thenReturn(payload);
    }

    @Test
    void getSummary_retornaKPIsDelControlTotales() {
        var summary = dashboardService.getSummary();

        assertThat(summary.totalCuentasProcesadas()).isEqualTo(150);
        assertThat(summary.totalCuentasConciliadas()).isEqualTo(134);
        assertThat(summary.totalCuentasConDiferencia()).isEqualTo(16);
        assertThat(summary.sumatoriaDiferenciaNeta()).isEqualByComparingTo("19.50");
    }

    @Test
    void getSummary_saldoFuente_coincideConControlTotales() {
        var summary = dashboardService.getSummary();
        assertThat(summary.sumatoriaSaldoFinalFuente()).isEqualByComparingTo("12455000.25");
    }
}
