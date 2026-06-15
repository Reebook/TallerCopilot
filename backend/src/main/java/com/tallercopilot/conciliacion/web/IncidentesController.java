package com.tallercopilot.conciliacion.web;

import com.tallercopilot.conciliacion.application.IncidentesService;
import com.tallercopilot.conciliacion.application.model.Incidente;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/incidentes")
public class IncidentesController {

    private final IncidentesService incidentesService;

    public IncidentesController(IncidentesService incidentesService) {
        this.incidentesService = incidentesService;
    }

    @GetMapping
    public List<Incidente> getIncidentes(@RequestParam(required = false) String severidad) {
        return incidentesService.getIncidentes(severidad);
    }
}
