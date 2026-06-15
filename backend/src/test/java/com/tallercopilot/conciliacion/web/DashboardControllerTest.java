package com.tallercopilot.conciliacion.web;

import com.tallercopilot.conciliacion.application.DashboardService;
import com.tallercopilot.conciliacion.application.DashboardSummary;
import com.tallercopilot.conciliacion.security.JwtProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tallercopilot.conciliacion.security.JwtAuthFilter;
import com.tallercopilot.conciliacion.security.SecurityConfig;

@WebMvcTest(DashboardController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtProvider.class})
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @Test
    void getSummary_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary"))
                                .andExpect(status().isForbidden());
    }

    @Test
    void getSummary_conToken_retornaJson() throws Exception {
        when(dashboardService.getSummary()).thenReturn(new DashboardSummary(
                10, 8, 2,
                new BigDecimal("1000.00"),
                new BigDecimal("999.50"),
                new BigDecimal("0.50")
        ));

        JwtProvider jwtProvider = new JwtProvider(
                "conciliacion-taller-secret-key-2026-secure", 86400000L
        );
        String token = jwtProvider.generate("admin", java.util.List.of("ADMIN"));

        mockMvc.perform(get("/api/dashboard/summary")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCuentasProcesadas").value(10))
                .andExpect(jsonPath("$.totalCuentasConciliadas").value(8));
    }
}
