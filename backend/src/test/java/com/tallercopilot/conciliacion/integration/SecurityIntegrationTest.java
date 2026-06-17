package com.tallercopilot.conciliacion.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration Tests - Security & Authorization")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Todos los endpoints protegidos requieren JWT")
    void testAllEndpointsRequireJwt() throws Exception {
        // Listar endpoints que deben estar protegidos
        String[] protectedEndpoints = {
                "/api/dashboard/summary",
                "/api/cuentas",
                "/api/cuentas/00000000000000000001",
                "/api/incidentes",
                "/api/export/cuentas.csv",
                "/api/export/cuentas.json",
                "/api/export/cuentas.pdf"
        };

        for (String endpoint : protectedEndpoints) {
            mockMvc.perform(get(endpoint)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andExpect(result -> {
                        // Verificar que no retorna 200 (sin protección)
                        int status = result.getResponse().getStatus();
                        assert status == 403 : "Endpoint " + endpoint + " no está protegido (status: " + status + ")";
                    });
        }
    }

    @Test
    @DisplayName("El endpoint de login no requiere autenticación")
    void testLoginEndpointDoesNotRequireAuth() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("CORS esta habilitado para peticiones desde frontend")
    void testCorsIsEnabled() throws Exception {
        mockMvc.perform(options("/api/dashboard/summary")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Bearer token debe estar en formato correcto")
    void testBearerTokenFormat() throws Exception {
        // Token sin "Bearer " prefix falla
        mockMvc.perform(get("/api/dashboard/summary")
                        .header("Authorization", "invalidtoken123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        // Authorization header sin token falla
        mockMvc.perform(get("/api/dashboard/summary")
                        .header("Authorization", "Bearer ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Las credenciales de lectura funcionan correctamente")
    void testLecturaCredentialsWork() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"lectura\",\"password\":\"lectura123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("Las credenciales de admin funcionan correctamente")
    void testAdminCredentialsWork() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("No se aceptan usuarios inexistentes")
    void testNonExistentUserRejected() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"inexistente\",\"password\":\"cualquier123\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Se rechazan contraseñas incorrectas")
    void testWrongPasswordRejected() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("El endpoint de health esta disponible sin autenticacion")
    void testHealthEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
