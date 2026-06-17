# Validacion de Rendimiento

Fecha: 2026-06-16
Objetivo: tiempo de carga de dashboard menor a 3 segundos.

## Metodo
- Ambiente local en Windows.
- Backend ejecutado en `http://localhost:8080` con Spring Boot y Java 17.
- Se hicieron 3 iteraciones de calentamiento.
- Se ejecutaron 20 iteraciones de medicion.
- En cada iteracion se midio:
  - `POST /api/auth/login` con usuario `lectura`.
  - `GET /api/dashboard/summary` con token JWT recien obtenido.
- Metricas registradas: promedio, p95 y maximo.

## Resultado
```json
{
  "runs": 20,
  "login_avg_ms": 10.54,
  "login_max_ms": 33.26,
  "dashboard_avg_ms": 10.57,
  "dashboard_p95_ms": 27.71,
  "dashboard_max_ms": 27.78,
  "objective_ms": 3000,
  "objective_met": true
}
```

## Conclusión
El objetivo de rendimiento se cumple:
- p95 del dashboard = 27.71 ms.
- Umbral objetivo = 3000 ms.

Margen aproximado frente al objetivo:
- 3000 - 27.71 = 2972.29 ms.

## Nota
Esta validacion corresponde al rendimiento de backend/API local. Para una validacion E2E completa de experiencia de usuario (frontend + red + navegador), se recomienda agregar medicion con Lighthouse o Playwright en un siguiente paso.
