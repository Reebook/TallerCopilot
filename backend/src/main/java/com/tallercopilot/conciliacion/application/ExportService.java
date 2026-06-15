package com.tallercopilot.conciliacion.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallercopilot.conciliacion.application.model.CuentaData;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

@Service
public class ExportService {

    private final ObjectMapper objectMapper;

    public ExportService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String exportCuentasAsCsv(List<CuentaData> cuentas) {
        StringBuilder csv = new StringBuilder();
        csv.append("cuentaContable,descripcionCuenta,codigoBanco,codigoSucursal,codigoMoneda,estado,severidad,diferenciaNeta\n");

        for (CuentaData cuenta : cuentas) {
            csv.append(sanitize(cuenta.cuentaMayor().cuentaContable())).append(",")
                    .append(sanitize(cuenta.cuentaMayor().descripcionCuenta())).append(",")
                    .append(sanitize(cuenta.cuentaMayor().codigoBanco())).append(",")
                    .append(sanitize(cuenta.cuentaMayor().codigoSucursal())).append(",")
                    .append(sanitize(cuenta.cuentaMayor().codigoMoneda())).append(",")
                    .append(sanitize(cuenta.estadoConciliacion().codigo())).append(",")
                    .append(sanitize(cuenta.estadoConciliacion().severidad())).append(",")
                    .append(cuenta.diferencias().diferenciaNeta())
                    .append("\n");
        }

        return csv.toString();
    }

    public String exportCuentasAsJson(List<CuentaData> cuentas) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(cuentas);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("No se pudo serializar la exportacion JSON", ex);
        }
    }

    public byte[] exportCuentasAsPdf(List<CuentaData> cuentas) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
                content.beginText();
                content.newLineAtOffset(50, 760);
                content.showText("Reporte de Cuentas Conciliadas");
                content.endText();

                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                float y = 735;
                for (CuentaData cuenta : cuentas) {
                    String line = String.format(
                            "%s | %s | Estado: %s | Dif: %s",
                            sanitize(cuenta.cuentaMayor().cuentaContable()),
                            sanitize(cuenta.cuentaMayor().descripcionCuenta()),
                            sanitize(cuenta.estadoConciliacion().codigo()),
                            cuenta.diferencias().diferenciaNeta()
                    );

                    if (y < 60) {
                        break;
                    }

                    content.beginText();
                    content.newLineAtOffset(50, y);
                    content.showText(line.length() > 120 ? line.substring(0, 120) : line);
                    content.endText();
                    y -= 14;
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo generar la exportacion PDF", ex);
        }
    }

    private String sanitize(String input) {
        if (input == null) {
            return "";
        }
        return input.replace(",", " ").replace("\"", "'");
    }
}
