package com.tallercopilot.conciliacion.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Contract Tests - API JSON Contracts")
class ApiContractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void authenticate() throws Exception {
        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andReturn();

        token = objectMapper.readValue(login.getResponse().getContentAsString(), java.util.Map.class)
                .get("token")
                .toString();
    }

    @Test
    @DisplayName("Contract: auth login response contains token as non-empty string")
    void contract_loginResponse() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"lectura\",\"password\":\"lectura123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("Contract: dashboard summary returns required numeric fields")
    void contract_dashboardSummary() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalCuentasProcesadas").isNumber())
                .andExpect(jsonPath("$.totalCuentasConciliadas").isNumber())
                .andExpect(jsonPath("$.totalCuentasConDiferencia").isNumber())
                .andExpect(jsonPath("$.sumatoriaSaldoFinalFuente").isNumber())
                .andExpect(jsonPath("$.sumatoriaSaldoFinalConciliado").isNumber())
                .andExpect(jsonPath("$.sumatoriaDiferenciaNeta").isNumber());
    }

    @Test
    @DisplayName("Contract: cuentas endpoint returns required nested fields")
    void contract_cuentasPayload() throws Exception {
        mockMvc.perform(get("/api/cuentas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].cuentaMayor.codigoBanco").isString())
                .andExpect(jsonPath("$[0].cuentaMayor.codigoSucursal").isString())
                .andExpect(jsonPath("$[0].cuentaMayor.codigoMoneda").isString())
                .andExpect(jsonPath("$[0].cuentaMayor.cuentaContable").isString())
                .andExpect(jsonPath("$[0].saldos.saldoFinalFuente").isNumber())
                .andExpect(jsonPath("$[0].diferencias.diferenciaNeta").isNumber())
                .andExpect(jsonPath("$[0].estadoConciliacion.codigo").isString())
                .andExpect(jsonPath("$[0].estadoConciliacion.severidad").isString())
                .andExpect(jsonPath("$[0].partidasConciliatorias").isArray());
    }

    @Test
    @DisplayName("Contract: incidentes endpoint returns required fields")
    void contract_incidentesPayload() throws Exception {
        mockMvc.perform(get("/api/incidentes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].codigo").isString())
                .andExpect(jsonPath("$[0].tipo").isString())
                .andExpect(jsonPath("$[0].cuentaContable").isString())
                .andExpect(jsonPath("$[0].mensaje").isString())
                .andExpect(jsonPath("$[0].severidad").isString());
    }

    @Test
    @DisplayName("Contract: export cuentas JSON returns attachment with JSON payload")
    void contract_exportJsonPayload() throws Exception {
        mockMvc.perform(get("/api/export/cuentas.json")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].cuentaMayor.cuentaContable").isString())
                .andExpect(jsonPath("$[0].estadoConciliacion.codigo").isString());
    }
}