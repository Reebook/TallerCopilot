# Estrategia de Despliegue y Versionado

Fecha: 2026-06-17

## Objetivo

Definir una estrategia auditable de despliegue por ambientes y versionado semantico para cumplir los criterios 7.6 del proceso de revision.

## Ambientes

- DEV: validacion funcional temprana y pruebas de integracion.
- QA: validacion de regresion, seguridad y pruebas E2E.
- PROD: entorno productivo con despliegue controlado.

## Flujo de promocion

1. Merge a rama de integracion (develop) con quality gate en verde.
2. Despliegue a DEV para smoke tests.
3. Promocion a QA con evidencia de pruebas (unit, integration, contract, e2e).
4. Aprobacion de release y etiquetado semantico.
5. Despliegue a PROD con ventana controlada y monitoreo activo.

## Criterios de aprobacion por ambiente

### DEV

- Build exitoso backend/frontend.
- Lint y analisis estatico en verde.
- Tests automatizados principales en verde.

### QA

- Cobertura minima requerida validada.
- Contract tests de API en verde.
- Pruebas E2E y pruebas de error funcional validadas.

### PROD

- Aprobacion manual de release.
- Tag semver publicado.
- Plan de rollback definido y probado.

## Versionado semantico

Se adopta SemVer: MAJOR.MINOR.PATCH.

- MAJOR: cambios incompatibles.
- MINOR: nuevas funcionalidades compatibles.
- PATCH: correcciones compatibles.

Reglas:

- No desplegar snapshots a PROD.
- Todo despliegue a PROD debe estar asociado a un tag semver.
- El changelog del release debe incluir impacto funcional, tecnico y de seguridad.

## Politica de ramas (equivalente a proteccion)

- main/master: solo merge via Pull Request.
- develop: rama de integracion.
- feature/*, fix/*: ramas de trabajo.

Controles:

- Merge bloqueado si quality gate falla.
- Requiere revision segun CODEOWNERS.
- Requiere checklist de PR completo.

## Rollback

- Rollback por version/tag previo validado.
- Reversion de imagen backend/frontend a version estable anterior.
- Verificacion post-rollback: health checks + endpoints criticos.

## Evidencia esperada por release

- Resultado de pipeline (build/test/lint/package).
- Version/tag semver aplicado.
- Registro de aprobacion de PR.
- Nota de release/changelog.
