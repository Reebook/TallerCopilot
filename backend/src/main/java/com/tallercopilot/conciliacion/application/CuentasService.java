package com.tallercopilot.conciliacion.application;

import com.tallercopilot.conciliacion.application.model.CuentaData;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CuentasService {

    private final ConciliacionDataService conciliacionDataService;

    public CuentasService(ConciliacionDataService conciliacionDataService) {
        this.conciliacionDataService = conciliacionDataService;
    }

    public List<CuentaData> getCuentas(
            String banco,
            String sucursal,
            String moneda,
            String cuenta,
            String estado,
            String severidad
    ) {
        return conciliacionDataService.getPayload().cuentas().stream()
                .filter(c -> banco == null || banco.equalsIgnoreCase(c.cuentaMayor().codigoBanco()))
                .filter(c -> sucursal == null || sucursal.equalsIgnoreCase(c.cuentaMayor().codigoSucursal()))
                .filter(c -> moneda == null || moneda.equalsIgnoreCase(c.cuentaMayor().codigoMoneda()))
                .filter(c -> cuenta == null || c.cuentaMayor().cuentaContable().contains(cuenta))
                .filter(c -> estado == null || estado.equalsIgnoreCase(c.estadoConciliacion().codigo()))
                .filter(c -> severidad == null || severidad.equalsIgnoreCase(c.estadoConciliacion().severidad()))
                .toList();
    }

    public CuentaData getCuentaById(String cuentaContable) {
        return conciliacionDataService.getPayload().cuentas().stream()
                .filter(c -> c.cuentaMayor().cuentaContable().equalsIgnoreCase(cuentaContable))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No existe la cuenta: " + cuentaContable));
    }
}
