# Diagrama de Componentes

Este documento describe la arquitectura de componentes de la solucion fullstack de conciliacion.

```mermaid
graph TD
    subgraph Frontend[Frontend React]
        UI[App UI]
        ROUTER[Rutas protegidas]
        API_CLIENT[Cliente API]
        UI --> ROUTER
        ROUTER --> API_CLIENT
    end

    subgraph Backend[Backend Spring Boot]
        AUTH_CTRL[AuthController]
        DASH_CTRL[DashboardController]
        CUENTAS_CTRL[CuentasController]
        INC_CTRL[IncidentesController]
        EXPORT_CTRL[ExportController]

        AUTH_SVC[AuthService]
        DASH_SVC[DashboardService]
        CUENTAS_SVC[CuentasService]
        INC_SVC[IncidentesService]
        EXPORT_SVC[ExportService]
        DATA_SVC[ConciliacionDataService]

        SECURITY[SecurityConfig + JwtAuthFilter + JwtProvider]

        AUTH_CTRL --> AUTH_SVC
        DASH_CTRL --> DASH_SVC
        CUENTAS_CTRL --> CUENTAS_SVC
        INC_CTRL --> INC_SVC
        EXPORT_CTRL --> EXPORT_SVC

        DASH_SVC --> DATA_SVC
        CUENTAS_SVC --> DATA_SVC
        INC_SVC --> DATA_SVC
        EXPORT_SVC --> DATA_SVC

        SECURITY --> AUTH_CTRL
        SECURITY --> DASH_CTRL
        SECURITY --> CUENTAS_CTRL
        SECURITY --> INC_CTRL
        SECURITY --> EXPORT_CTRL
    end

    subgraph Data[Fuente de datos]
        JSON[JSON de conciliacion IBM i]
    end

    API_CLIENT --> AUTH_CTRL
    API_CLIENT --> DASH_CTRL
    API_CLIENT --> CUENTAS_CTRL
    API_CLIENT --> INC_CTRL
    API_CLIENT --> EXPORT_CTRL

    DATA_SVC --> JSON
```

## Responsabilidades

- Frontend: autenticacion, visualizacion de KPIs, filtros, detalle, incidentes y exportacion.
- Backend: autenticacion JWT, reglas de negocio y exposicion de endpoints REST.
- Data service: lectura y transformacion de payload de conciliacion desde fuente JSON.

## Notas

- Los controladores solo orquestan requests/responses HTTP.
- La logica de autenticacion y rol se centraliza en `AuthService`.
- La seguridad transversal se aplica mediante filtro JWT y configuracion de Spring Security.
