package com.tallercopilot.conciliacion.web;

import com.tallercopilot.conciliacion.application.CuentasService;
import com.tallercopilot.conciliacion.application.ExportService;
import java.nio.charset.StandardCharsets;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final CuentasService cuentasService;
    private final ExportService exportService;

    public ExportController(CuentasService cuentasService, ExportService exportService) {
        this.cuentasService = cuentasService;
        this.exportService = exportService;
    }

    @GetMapping("/cuentas.csv")
    public ResponseEntity<byte[]> exportCuentasCsv(
            @RequestParam(required = false) String banco,
            @RequestParam(required = false) String sucursal,
            @RequestParam(required = false) String moneda,
            @RequestParam(required = false) String cuenta,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String severidad
    ) {
        var cuentas = cuentasService.getCuentas(banco, sucursal, moneda, cuenta, estado, severidad);
        String csv = exportService.exportCuentasAsCsv(cuentas);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("cuentas-conciliacion.csv").build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping("/cuentas.json")
    public ResponseEntity<byte[]> exportCuentasJson(
            @RequestParam(required = false) String banco,
            @RequestParam(required = false) String sucursal,
            @RequestParam(required = false) String moneda,
            @RequestParam(required = false) String cuenta,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String severidad
    ) {
        var cuentas = cuentasService.getCuentas(banco, sucursal, moneda, cuenta, estado, severidad);
        String json = exportService.exportCuentasAsJson(cuentas);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentDisposition(ContentDisposition.attachment().filename("cuentas-conciliacion.json").build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(json.getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping("/cuentas.pdf")
    public ResponseEntity<byte[]> exportCuentasPdf(
            @RequestParam(required = false) String banco,
            @RequestParam(required = false) String sucursal,
            @RequestParam(required = false) String moneda,
            @RequestParam(required = false) String cuenta,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String severidad
    ) {
        var cuentas = cuentasService.getCuentas(banco, sucursal, moneda, cuenta, estado, severidad);
        byte[] pdf = exportService.exportCuentasAsPdf(cuentas);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("cuentas-conciliacion.pdf").build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
}
