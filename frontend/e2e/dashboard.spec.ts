import { expect, test } from "@playwright/test";

test("login y visualización de dashboard", async ({ page }) => {
  await page.route("**/api/auth/login", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({ token: "token-e2e" }),
    });
  });

  await page.route("**/api/dashboard/summary", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        totalCuentasProcesadas: 5,
        totalCuentasConciliadas: 4,
        totalCuentasConDiferencia: 1,
        sumatoriaSaldoFinalFuente: 1000,
        sumatoriaSaldoFinalConciliado: 980,
        sumatoriaDiferenciaNeta: 20,
      }),
    });
  });

  await page.route("**/api/cuentas", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify([
        {
          cuentaMayor: {
            cuentaContable: "1101",
            descripcionCuenta: "Caja General",
            codigoBanco: "01",
            codigoSucursal: "001",
            codigoMoneda: "COP",
          },
          saldos: {
            saldoFinalFuente: 1000,
            saldoFinalConciliado: 980,
          },
          estadoConciliacion: {
            codigo: "PARCIAL",
            severidad: "MEDIA",
          },
          diferencias: {
            diferenciaNeta: 20,
          },
          partidasConciliatorias: [],
        },
      ]),
    });
  });

  await page.route("**/api/incidentes", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify([
        {
          codigo: "INC-001",
          cuentaContable: "1101",
          mensaje: "Incidente de prueba",
          severidad: "MEDIA",
        },
      ]),
    });
  });

  await page.goto("/");
  await expect(page.getByRole("heading", { name: "Acceso al Dashboard" })).toBeVisible();

  await page.getByRole("button", { name: "Entrar" }).click();

  await expect(page.getByRole("heading", { name: "Dashboard de Conciliacion" })).toBeVisible();
  await expect(page.getByText("Cuentas procesadas")).toBeVisible();
  await expect(page.getByText("Incidentes")).toBeVisible();
});
