package com.tallercopilot.conciliacion.application;

import com.tallercopilot.conciliacion.application.model.Incidente;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class IncidentesService {

    private final ConciliacionDataService conciliacionDataService;

    public IncidentesService(ConciliacionDataService conciliacionDataService) {
        this.conciliacionDataService = conciliacionDataService;
    }

    public List<Incidente> getIncidentes(String severidad) {
        return conciliacionDataService.getPayload().incidentes().stream()
                .filter(i -> severidad == null || severidad.equalsIgnoreCase(i.severidad()))
                .toList();
    }
}
