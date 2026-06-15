package com.tallercopilot.conciliacion.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallercopilot.conciliacion.application.model.ConciliacionPayload;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class ConciliacionDataService {

    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final ResourceLoader resourceLoader;
    private final String sourcePath;

    private volatile ConciliacionPayload cachedPayload;

    public ConciliacionDataService(
            ObjectMapper objectMapper,
            Validator validator,
            ResourceLoader resourceLoader,
            @Value("${app.conciliacion.source:classpath:data/conciliacion-sample.json}") String sourcePath
    ) {
        this.objectMapper = objectMapper;
        this.validator = validator;
        this.resourceLoader = resourceLoader;
        this.sourcePath = sourcePath;
    }

    @PostConstruct
    public void init() {
        this.cachedPayload = readAndValidate();
    }

    public ConciliacionPayload getPayload() {
        if (cachedPayload == null) {
            cachedPayload = readAndValidate();
        }
        return cachedPayload;
    }

    public ConciliacionPayload reload() {
        cachedPayload = readAndValidate();
        return cachedPayload;
    }

    private ConciliacionPayload readAndValidate() {
        try {
            Resource resource = resourceLoader.getResource(sourcePath);
            if (!resource.exists()) {
                throw new IllegalStateException("No se encontro el archivo JSON de conciliacion en: " + sourcePath);
            }

            try (InputStream inputStream = resource.getInputStream()) {
                ConciliacionPayload payload = objectMapper.readValue(inputStream, ConciliacionPayload.class);
                validate(payload);
                return payload;
            }
        } catch (IOException ex) {
            throw new IllegalStateException("No fue posible leer el JSON de conciliacion", ex);
        }
    }

    private void validate(ConciliacionPayload payload) {
        Set<ConstraintViolation<ConciliacionPayload>> violations = validator.validate(payload);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(v -> v.getPropertyPath() + " " + v.getMessage())
                    .collect(Collectors.joining("; "));
            throw new IllegalStateException("JSON de conciliacion invalido: " + errorMessage);
        }
    }
}
