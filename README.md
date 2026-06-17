# TallerCopilot - Conciliacion Fullstack

Aplicacion fullstack para conciliacion contable con backend Spring Boot y frontend React.

## Stack

- Backend: Java 17 + Spring Boot 3.5 + Maven
- Frontend: React 18 + Vite + TypeScript
- Seguridad: JWT (usuarios in-memory)
- Testing backend: JUnit 5
- Testing frontend: Vitest + React Testing Library + Playwright

## Requisitos de entorno

- JDK 17 instalado
- Node.js 20+
- Maven 3.9+

Si en Windows `mvn -v` muestra otra version de Java, configure `JAVA_HOME` a JDK 17 antes de ejecutar backend.

## Estructura

- `backend/`: API REST Spring Boot
- `frontend/`: UI React
- `docs/plan/roadmap.md`: roadmap y checklist de avance
- `docs/plan/performance-validation.md`: evidencia de rendimiento
- `instructions/`: requerimientos base del taller

## Ejecucion local

### Backend

```bash
cd backend
mvn spring-boot:run
```

Disponible en `http://localhost:8080`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Disponible en `http://localhost:5173`.

## Endpoints principales

- `POST /api/auth/login`
- `GET /api/dashboard/summary`
- `GET /api/cuentas`
- `GET /api/cuentas/{cuentaContable}`
- `GET /api/incidentes`
- `GET /api/export/cuentas.csv`
- `GET /api/export/cuentas.json`
- `GET /api/export/cuentas.pdf`
- `GET /swagger-ui.html`
- `GET /v3/api-docs`

Ejemplos de filtros:
- `/api/cuentas?banco=01&sucursal=001&moneda=USD&cuenta=1101&estado=PARCIAL&severidad=MEDIA`
- `/api/incidentes?severidad=BAJA`

## Pruebas

### Backend

```bash
cd backend
mvn test
```

Resultado validado: **40 tests**, `Failures: 0`, `Errors: 0`.

### Frontend unitarias

```bash
cd frontend
npm run test
```

Resultado validado: **3 tests** pasando (Vitest/RTL).

### Frontend E2E

```bash
cd frontend
npx playwright install
npm run test:e2e
```

Resultado validado: **1 test** E2E pasando (Playwright).

### Ejecucion completa frontend

```bash
cd frontend
npm run test:all
```

## CI/CD

Workflow en `.github/workflows/ci.yml` con quality gate:
- backend tests
- frontend build
- docker build backend/frontend
- quality gate final que falla si cualquier job falla

## Evidencia de rendimiento

Ver `docs/plan/performance-validation.md`.

Resumen:
- objetivo: `< 3000 ms`
- p95 dashboard: `27.71 ms`
- estado: **cumplido**
