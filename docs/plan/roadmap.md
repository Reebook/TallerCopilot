## Plan: Conciliacion Fullstack con Spring Boot y React

Solucion fullstack con Java Spring Boot (backend) y React (frontend) para consumir JSON de conciliacion desde IBM i, validarlo, exponer API, visualizar dashboard, gestionar incidentes y exportar resultados.

**Estado actual**
- En progreso avanzado.
- Backend implementado con ingestión, validación, filtros, incidentes, exportaciones y seguridad JWT.
- Frontend implementado con login, KPIs, filtros, detalle por cuenta, incidentes y exportación.
- Suite backend validada en Java 17 con `mvn test` en verde.

**Checklist de avance**
- [x] Spring Security con roles JWT
- [x] Logs estructurados y CORS
- [x] Unit tests backend (JUnit 5)
- [x] Dockerfile + docker-compose base
- [ ] OpenAPI/Swagger
- [ ] Integration tests backend
- [ ] Tests frontend (Vitest/RTL) y E2E
- [ ] CI/CD con quality gate
- [ ] Validación de rendimiento (< 3 segundos)
- [ ] Documentación final y evidencia

**Fase 0: Fundacion tecnica**
1. Java 21 + Spring Boot 3.x + Maven.
2. React 18 + Vite + TypeScript.
3. Arquitectura limpia/hexagonal en backend y arquitectura por features en frontend.
4. Calidad: Checkstyle/Spotless, ESLint/Prettier, convenciones de ramas y PR.

**Fase 1: Contrato e ingesta**
1. DTOs y modelos de conciliacion.
2. Validacion JSON Schema + Bean Validation.
3. Ingesta por archivo, endpoint o storage intermedio.
4. Trazabilidad de corrida (id, timestamps, estado).
5. Catalogo de errores funcionales.

**Fase 2: API y exportacion**
1. Endpoints para resumen, cuentas, detalle, partidas e incidentes.
2. Filtros por banco, sucursal, moneda, periodo, cuenta, estado y severidad.
3. Paginacion y ordenamiento.
4. Exportacion CSV/JSON/PDF.
5. OpenAPI/Swagger.

**Fase 3: Frontend React**
1. Rutas protegidas y layout.
2. Dashboard KPI con controlTotales.
3. Vista detalle por cuenta y partidas.
4. Panel de incidentes con severidad.
5. Graficas obligatorias del taller.
6. Exportacion desde UI con filtros activos.

**Fase 4: Seguridad y observabilidad**
1. Spring Security con roles lectura/admin (JWT u OIDC).
2. Validacion y sanitizacion de entradas.
3. Logs estructurados con correlacion.
4. Metricas con Actuator/Prometheus.
5. Manejo centralizado de excepciones.

**Fase 5: Pruebas y entrega**
1. Unit tests backend (JUnit 5, Mockito).
2. Integration tests backend (Spring Boot Test, Testcontainers opcional).
3. Contract tests de JSON.
4. Tests frontend (Vitest, RTL) + E2E (Playwright).
5. Objetivo rendimiento dashboard < 3 segundos.
6. CI/CD con quality gate y empaquetado Docker.
7. Documentacion y evidencia final.

**Alcance**
- Incluye ingesta, validacion, dashboard, filtros, incidentes, exportacion, seguridad y observabilidad.
- Excluye cambios en IBM i y conciliacion automatica en origen.
