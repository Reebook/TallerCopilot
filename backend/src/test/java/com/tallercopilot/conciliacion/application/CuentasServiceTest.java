package com.tallercopilot.conciliacion.application;

import com.tallercopilot.conciliacion.application.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuentasServiceTest {

    @Mock
    private ConciliacionDataService conciliacionDataService;

    @InjectMocks
    private CuentasService cuentasService;

    private CuentaData cuentaParcial;
    private CuentaData cuentaConciliada;

    @BeforeEach
    void setUp() {
        cuentaParcial = buildCuenta("01", "001", "USD", "11010101", "CAJA GENERAL", "PARCIAL", "MEDIA", new BigDecimal("0.50"));
        cuentaConciliada = buildCuenta("01", "001", "USD", "11010102", "CAJA CHICA", "CONCILIADA", "BAJA", BigDecimal.ZERO);

        ConciliacionPayload payload = new ConciliacionPayload(
                null, null, null,
                List.of(cuentaParcial, cuentaConciliada),
                new ControlTotales(2, 1, 1, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                List.of()
        );

        when(conciliacionDataService.getPayload()).thenReturn(payload);
    }

    @Test
    void getCuentas_sinFiltros_retornaTodas() {
        var result = cuentasService.getCuentas(null, null, null, null, null, null);
        assertThat(result).hasSize(2);
    }

    @Test
    void getCuentas_filtrandoPorEstadoParcial_retornaSolo1() {
        var result = cuentasService.getCuentas(null, null, null, null, "PARCIAL", null);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).estadoConciliacion().codigo()).isEqualTo("PARCIAL");
    }

    @Test
    void getCuentas_filtrandoPorEstadoConciliada_retornaSolo1() {
        var result = cuentasService.getCuentas(null, null, null, null, "CONCILIADA", null);
        assertThat(result).hasSize(1);
    }

    @Test
    void getCuentas_filtrandoPorBancoDistinto_retornaVacio() {
        var result = cuentasService.getCuentas("99", null, null, null, null, null);
        assertThat(result).isEmpty();
    }

    @Test
    void getCuentaById_existente_retornaCuenta() {
        var result = cuentasService.getCuentaById("11010101");
        assertThat(result.cuentaMayor().cuentaContable()).isEqualTo("11010101");
    }

    @Test
    void getCuentaById_inexistente_lanzaExcepcion() {
        assertThatThrownBy(() -> cuentasService.getCuentaById("99999999"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99999999");
    }

    private CuentaData buildCuenta(
            String banco, String sucursal, String moneda,
            String cuenta, String descripcion,
            String estado, String severidad,
            BigDecimal diferencia
    ) {
        return new CuentaData(
                new CuentaMayor(banco, sucursal, moneda, cuenta, descripcion),
                new Saldos(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                new Diferencias(diferencia, diferencia.abs(), new BigDecimal("1.00"), false, ""),
                new EstadoConciliacion(estado, "", severidad, false),
                List.of()
        );
    }
}
