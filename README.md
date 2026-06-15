# TallerCopilot - Conciliacion Fullstack

Implementacion inicial del proyecto usando:
- Backend: Java 21 + Spring Boot 3
- Frontend: React 18 + Vite + TypeScript

## Requisito importante de entorno

El backend usa `record` de Java, por lo que Maven debe ejecutarse con Java 21.

Si `mvn -v` muestra Java 8, configure `JAVA_HOME` a JDK 21 antes de compilar o ejecutar.

## Estructura

- `backend/`: API Spring Boot con endpoint inicial de dashboard.
- `frontend/`: UI React con vista de KPIs conectada a `/api/dashboard/summary`.
- `docs/plan/roadmap.md`: Plan de implementacion por fases.
- `instructions/`: Requerimientos base del taller.

## Ejecutar backend

```bash
cd backend
mvn spring-boot:run
```

Backend disponible en `http://localhost:8080`.

Endpoint inicial:
- `GET /api/dashboard/summary`

Endpoints implementados en esta iteracion:
- `GET /api/dashboard/summary`
- `GET /api/cuentas`
- `GET /api/cuentas/{cuentaContable}`
- `GET /api/incidentes`
- `GET /api/export/cuentas.csv`
- `GET /api/export/cuentas.json`
- `GET /api/export/cuentas.pdf`

Filtros disponibles:
- `/api/cuentas?banco=01&sucursal=001&moneda=USD&cuenta=1101&estado=PARCIAL&severidad=MEDIA`
- `/api/incidentes?severidad=BAJA`
- `/api/export/cuentas.csv?estado=PARCIAL&severidad=MEDIA`
- `/api/export/cuentas.json?estado=CONCILIADA`
- `/api/export/cuentas.pdf?estado=PARCIAL`

Capacidades UI implementadas:
- Vista de KPIs de conciliacion.
- Tabla de cuentas con filtros por estado y severidad.
- Detalle por cuenta seleccionada con partidas conciliatorias.
- Panel de incidentes.
- Graficas simples de distribucion por estado y top diferencias.
- Botones de exportacion CSV, JSON y PDF usando los filtros activos.

## Ejecutar frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend disponible en `http://localhost:5173`.

## Proximos pasos de implementacion

1. Implementar carga por archivo externo y endpoint de recarga de dataset para QA.
2. Agregar exportacion PDF del consolidado (RSA-10).
3. Incorporar seguridad con Spring Security y roles (RNSA-01).
4. Completar pruebas unitarias/integracion y pipeline CI/CD (seccion 7).
5. Agregar graficas avanzadas (serie temporal de diferencias y comparativo fuente vs conciliado).
