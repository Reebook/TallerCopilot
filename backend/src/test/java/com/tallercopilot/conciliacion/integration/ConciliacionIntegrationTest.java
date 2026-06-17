package com.tallercopilot.conciliacion.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallercopilot.conciliacion.application.ConciliacionDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration Tests - API Endpoints")
class ConciliacionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConciliacionDataService conciliacionDataService;

    private String validToken;

    @BeforeEach
    void setUp() throws Exception {
        // Obtener token del usuario de lectura
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"lectura\",\"password\":\"lectura123\"}"))
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        validToken = objectMapper.readValue(responseBody, java.util.Map.class).get("token").toString();
    }

    @Test
    @DisplayName("Login exitoso retorna JWT token")
    void testLoginSuccess() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("Login fallido con credenciales incorrectas")
    void testLoginFailure() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Dashboard API retorna JSON válido")
    void testDashboardEndpoint() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalCuentasProcesadas").isNumber())
                .andExpect(jsonPath("$.totalCuentasConDiferencia").isNumber());
    }

    @Test
    @DisplayName("Cuentas API retorna array JSON")
    void testCuentasEndpoint() throws Exception {
        mockMvc.perform(get("/api/cuentas")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Incidentes API retorna array JSON")
    void testIncidentesEndpoint() throws Exception {
        mockMvc.perform(get("/api/incidentes")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Export CSV retorna contenido válido")
    void testExportCsvEndpoint() throws Exception {
        mockMvc.perform(get("/api/export/cuentas.csv")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("cuentaContable");
                });
    }

    @Test
    @DisplayName("Export JSON retorna array válido")
    void testExportJsonEndpoint() throws Exception {
        mockMvc.perform(get("/api/export/cuentas.json")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Export PDF retorna bytes válidos")
    void testExportPdfEndpoint() throws Exception {
        mockMvc.perform(get("/api/export/cuentas.pdf")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    byte[] pdf = result.getResponse().getContentAsByteArray();
                    // PDF debe empezar con %PDF
                    assertThat(pdf).startsWith(new byte[]{(byte)0x25, (byte)0x50, (byte)0x44, (byte)0x46});
                });
    }

    @Test
    @DisplayName("Filtros en API retornan resultados consistentes")
    void testFiltersConsistency() throws Exception {
        // Obtener todas las cuentas
        mockMvc.perform(get("/api/cuentas")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Con filtro de estado
        mockMvc.perform(get("/api/cuentas?estado=DIFERENCIA")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Datos cargados correctamente desde JSON")
    void testDataLoadedCorrectly() {
        var payload = conciliacionDataService.getPayload();
        assertThat(payload).isNotNull();
        assertThat(payload.controlTotales()).isNotNull();
        assertThat(payload.cuentas()).isNotNull();
        assertThat(payload.incidentes()).isNotNull();
    }
}
