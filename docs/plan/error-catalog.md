# Catalogo de Errores Funcionales

Este catalogo define los errores funcionales y tecnicos esperados por la API, con su codigo HTTP y mensaje base.

## Convenciones

- Formato de respuesta de error: `{"timestamp":...,"status":...,"error":...,"message":...,"path":...}`.
- Los mensajes deben ser claros para frontend y trazables en logs.
- No exponer secretos ni detalles internos de infraestructura.

## Errores de autenticacion

| Codigo | HTTP | Escenario | Mensaje base | Origen |
|---|---|---|---|---|
| AUTH-001 | 401 | Credenciales invalidas en login | Credenciales invalidas | AuthService |
| AUTH-002 | 401 | Token JWT ausente o invalido | Unauthorized | Filtro JWT/Spring Security |
| AUTH-003 | 403 | Usuario autenticado sin rol requerido | Forbidden | Spring Security |

## Errores de validacion de entrada

| Codigo | HTTP | Escenario | Mensaje base | Origen |
|---|---|---|---|---|
| VAL-001 | 400 | Campos obligatorios faltantes | Validation failed | Bean Validation |
| VAL-002 | 400 | Parametro de filtro invalido | Request parameter invalid | Controladores/API |

## Errores de negocio

| Codigo | HTTP | Escenario | Mensaje base | Origen |
|---|---|---|---|---|
| BIZ-001 | 404 | Cuenta no encontrada | Cuenta no encontrada | CuentasService |
| BIZ-002 | 404 | Recurso de conciliacion no encontrado | Recurso no encontrado | Servicios de dominio |
| BIZ-003 | 409 | Estado inconsistente para operacion | Conflicto de estado de negocio | Servicios de dominio |

## Errores de exportacion

| Codigo | HTTP | Escenario | Mensaje base | Origen |
|---|---|---|---|---|
| EXP-001 | 500 | Error al generar CSV | Error al generar exportacion CSV | ExportService |
| EXP-002 | 500 | Error al generar JSON | Error al generar exportacion JSON | ExportService |
| EXP-003 | 500 | Error al generar PDF | Error al generar exportacion PDF | ExportService |

## Errores tecnicos generales

| Codigo | HTTP | Escenario | Mensaje base | Origen |
|---|---|---|---|---|
| SYS-001 | 500 | Error interno no controlado | Error interno del servidor | ApiExceptionHandler |
| SYS-002 | 503 | Dependencia no disponible | Servicio temporalmente no disponible | Capa de infraestructura |

## Recomendaciones de evolucion

- Estandarizar codigos en una enumeracion compartida backend/frontend.
- Agregar pruebas de contrato para verificar estructura de errores.
- Versionar cambios del catalogo junto con cambios de API.
