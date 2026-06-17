package com.tallercopilot.conciliacion.integration;

import com.tallercopilot.conciliacion.application.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Integration Tests - Business Services")
class BusinessServiceIntegrationTest {

    @Autowired
    private ConciliacionDataService conciliacionDataService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private CuentasService cuentasService;

    @Autowired
    private IncidentesService incidentesService;

    @Autowired
    private ExportService exportService;

    @Test
    @DisplayName("ConciliacionDataService carga datos correctamente")
    void testDataServiceLoadsDataOnStartup() {
        var payload = conciliacionDataService.getPayload();
        assertThat(payload).isNotNull();
        assertThat(payload.controlTotales()).isNotNull();
        assertThat(payload.cuentas()).isNotNull();
        assertThat(payload.incidentes()).isNotNull();
    }

    @Test
    @DisplayName("DashboardService calcula resumen sin excepciones")
    void testDashboardSummaryCalculation() {
        var summary = dashboardService.getSummary();
        assertThat(summary).isNotNull();
        assertThat(summary.totalCuentasProcesadas()).isGreaterThanOrEqualTo(0);
        assertThat(summary.sumatoriaSaldoFinalFuente()).isNotNull();
        assertThat(summary.sumatoriaDiferenciaNeta()).isNotNull();
    }

    @Test
    @DisplayName("CuentasService retorna lista sin excepciones")
    void testCuentasGetAll() {
        var cuentas = cuentasService.getCuentas(null, null, null, null, null, null);
        assertThat(cuentas).isNotNull();
        assertThat(cuentas.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("CuentasService filtra por estado sin excepciones")
    void testCuentasFilterByEstado() {
        var cuentas = cuentasService.getCuentas(null, null, null, null, "DIFERENCIA", null);
        assertThat(cuentas).isNotNull();
        // Todos deben tener estado DIFERENCIA (case-insensitive)
        cuentas.forEach(c -> 
            assertThat(c.estadoConciliacion().codigo()).isEqualToIgnoringCase("DIFERENCIA")
        );
    }

    @Test
    @DisplayName("CuentasService filtra por severidad sin excepciones")
    void testCuentasFilterBySeveridad() {
        var cuentas = cuentasService.getCuentas(null, null, null, null, null, "ALTA");
        assertThat(cuentas).isNotNull();
        // Todos deben tener severidad ALTA (case-insensitive)
        cuentas.forEach(c -> 
            assertThat(c.estadoConciliacion().severidad()).isEqualToIgnoringCase("ALTA")
        );
    }

    @Test
    @DisplayName("CuentasService obtiene por ID sin excepciones")
    void testCuentasGetById() {
        var allCuentas = cuentasService.getCuentas(null, null, null, null, null, null);
        assertThat(allCuentas).isNotEmpty();
        
        String id = allCuentas.get(0).cuentaMayor().cuentaContable();
        var cuenta = cuentasService.getCuentaById(id);
        
        assertThat(cuenta).isNotNull();
        assertThat(cuenta.cuentaMayor().cuentaContable()).isEqualTo(id);
    }

    @Test
    @DisplayName("IncidentesService retorna incidentes sin excepciones")
    void testIncidentesGetAll() {
        var incidentes = incidentesService.getIncidentes(null);
        assertThat(incidentes).isNotNull();
    }

    @Test
    @DisplayName("ExportService exporta a CSV sin excepciones")
    void testExportToCsv() {
        var cuentas = cuentasService.getCuentas(null, null, null, null, null, null);
        String csv = exportService.exportCuentasAsCsv(cuentas);
        
        assertThat(csv).isNotNull();
        assertThat(csv).isNotEmpty();
        assertThat(csv).contains("cuentaContable");
    }

    @Test
    @DisplayName("ExportService exporta a JSON sin excepciones")
    void testExportToJson() {
        var cuentas = cuentasService.getCuentas(null, null, null, null, null, null);
        String json = exportService.exportCuentasAsJson(cuentas);
        
        assertThat(json).isNotNull();
        assertThat(json).isNotEmpty();
        assertThat(json).startsWith("[");
    }

    @Test
    @DisplayName("ExportService exporta a PDF sin excepciones")
    void testExportToPdf() {
        var cuentas = cuentasService.getCuentas(null, null, null, null, null, null);
        byte[] pdf = exportService.exportCuentasAsPdf(cuentas);
        
        assertThat(pdf).isNotNull();
        assertThat(pdf).isNotEmpty();
        // PDF files start with %PDF
        assertThat(pdf).startsWith(new byte[]{(byte)0x25, (byte)0x50, (byte)0x44, (byte)0x46});
    }

    @Test
    @DisplayName("Todos los servicios funcionan en conjunto")
    void testAllServicesIntegration() {
        // Cargar datos
        var payload = conciliacionDataService.getPayload();
        assertThat(payload).isNotNull();
        
        // Generar resumen
        var summary = dashboardService.getSummary();
        assertThat(summary).isNotNull();
        assertThat(summary.totalCuentasProcesadas()).isEqualTo(payload.cuentas().size());
        
        // Obtener cuentas
        var cuentas = cuentasService.getCuentas(null, null, null, null, null, null);
        assertThat(cuentas).isNotEmpty();
        
        // Obtener incidentes
        var incidentes = incidentesService.getIncidentes(null);
        assertThat(incidentes).isNotNull();
        
        // Exportar
        String csv = exportService.exportCuentasAsCsv(cuentas);
        String json = exportService.exportCuentasAsJson(cuentas);
        byte[] pdf = exportService.exportCuentasAsPdf(cuentas);
        
        assertThat(csv).isNotEmpty();
        assertThat(json).isNotEmpty();
        assertThat(pdf).isNotEmpty();
    }
}
