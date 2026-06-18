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

### Variables de entorno requeridas (seguridad)

El proyecto no incluye secretos ni passwords por defecto en codigo.

- `APP_SECURITY_JWT_SECRET` (requerida)
- `APP_AUTH_ADMIN_PASSWORD` (requerida)
- `APP_AUTH_LECTURA_PASSWORD` (requerida)
- `APP_AUTH_ADMIN_USERNAME` (opcional, default `admin`)
- `APP_AUTH_LECTURA_USERNAME` (opcional, default `lectura`)

Puede usar `.env.example` como plantilla para crear su `.env` local.

Si en Windows `mvn -v` muestra otra version de Java, configure `JAVA_HOME` a JDK 17 antes de ejecutar backend.

## Estructura

- `backend/`: API REST Spring Boot
- `frontend/`: UI React
- `docs/plan/roadmap.md`: roadmap y checklist de avance
- `docs/plan/component-diagram.md`: diagrama de componentes versionado
- `docs/plan/error-catalog.md`: catalogo de errores y codigos funcionales
- `docs/plan/performance-validation.md`: evidencia de rendimiento
- `instructions/`: requerimientos base del taller

## Ejecucion local

### Backend

```bash
copy ..\\.env.example ..\\.env
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

Cobertura y umbral minimo (80%) en backend:

```bash
cd backend
mvn verify
```

Esto genera reporte JaCoCo en `backend/target/site/jacoco` y falla si la cobertura de lineas es menor a 80%.

### Frontend unitarias

```bash
cd frontend
npm run test
```

Resultado validado: **5 tests** pasando (Vitest/RTL).

Cobertura y umbral minimo (80%) en frontend:

```bash
cd frontend
npm run test:coverage
```

Genera reporte de cobertura (text/html/lcov) y valida umbrales globales.

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
- backend tests + coverage (JaCoCo 80%)
- frontend build
- frontend tests + coverage (Vitest 80%)
- docker build backend/frontend
- quality gate final que falla si cualquier job falla

## Contract Tests API

Se agrego una suite dedicada de contrato JSON/API en backend:
- `ApiContractIntegrationTest` valida estructura y tipos de payloads para login, dashboard, cuentas, incidentes y export JSON.

## Docker Compose

Antes de ejecutar compose, cree el archivo `.env` en la raiz del repositorio a partir de `.env.example` y complete valores reales.

## Evidencia de rendimiento

Ver `docs/plan/performance-validation.md`.

## Documentacion tecnica adicional

- Diagrama de componentes: `docs/plan/component-diagram.md`
- Catalogo de errores funcionales: `docs/plan/error-catalog.md`

Resumen:
- objetivo: `< 3000 ms`
- p95 dashboard: `27.71 ms`
- estado: **cumplido**
