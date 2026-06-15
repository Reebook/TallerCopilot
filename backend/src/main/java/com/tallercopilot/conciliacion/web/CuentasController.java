package com.tallercopilot.conciliacion.web;

import com.tallercopilot.conciliacion.application.CuentasService;
import com.tallercopilot.conciliacion.application.model.CuentaData;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cuentas")
public class CuentasController {

    private final CuentasService cuentasService;

    public CuentasController(CuentasService cuentasService) {
        this.cuentasService = cuentasService;
    }

    @GetMapping
    public List<CuentaData> getCuentas(
            @RequestParam(required = false) String banco,
            @RequestParam(required = false) String sucursal,
            @RequestParam(required = false) String moneda,
            @RequestParam(required = false) String cuenta,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String severidad
    ) {
        return cuentasService.getCuentas(banco, sucursal, moneda, cuenta, estado, severidad);
    }

    @GetMapping("/{cuentaContable}")
    @ResponseStatus(HttpStatus.OK)
    public CuentaData getCuentaById(@PathVariable String cuentaContable) {
        return cuentasService.getCuentaById(cuentaContable);
    }
}
