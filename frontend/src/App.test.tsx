import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { App } from "./App";

type MockResponse = {
  ok: boolean;
  json: () => Promise<unknown>;
};

describe("App", () => {
  beforeEach(() => {
    vi.stubGlobal("fetch", vi.fn());
    vi.stubGlobal("open", vi.fn());
  });

  afterEach(() => {
    vi.unstubAllGlobals();
    vi.clearAllMocks();
  });

  it("muestra formulario de login al inicio", () => {
    render(<App />);

    expect(screen.getByRole("heading", { name: "Acceso al Dashboard" })).toBeInTheDocument();
    expect(screen.getByLabelText("Usuario")).toBeInTheDocument();
    expect(screen.getByLabelText("Contraseña")).toBeInTheDocument();
  });

  it("muestra error cuando login falla", async () => {
    const fetchMock = vi.mocked(fetch);
    fetchMock.mockResolvedValueOnce({
      ok: false,
      json: async () => ({}),
    } as MockResponse as Response);

    render(<App />);
    await userEvent.click(screen.getByRole("button", { name: "Entrar" }));

    expect(await screen.findByText("Credenciales inválidas")).toBeInTheDocument();
  });

  it("hace login y renderiza dashboard", async () => {
    const fetchMock = vi.mocked(fetch);

    fetchMock
      .mockResolvedValueOnce({
        ok: true,
        json: async () => ({ token: "token-demo" }),
      } as MockResponse as Response)
      .mockResolvedValueOnce({
        ok: true,
        json: async () => ({
          totalCuentasProcesadas: 3,
          totalCuentasConciliadas: 2,
          totalCuentasConDiferencia: 1,
          sumatoriaSaldoFinalFuente: 100,
          sumatoriaSaldoFinalConciliado: 90,
          sumatoriaDiferenciaNeta: 10,
        }),
      } as MockResponse as Response)
      .mockResolvedValueOnce({
        ok: true,
        json: async () => [
          {
            cuentaMayor: {
              cuentaContable: "1001",
              descripcionCuenta: "Caja",
              codigoBanco: "01",
              codigoSucursal: "001",
              codigoMoneda: "COP",
            },
            saldos: {
              saldoFinalFuente: 100,
              saldoFinalConciliado: 90,
            },
            estadoConciliacion: {
              codigo: "CONCILIADA",
              severidad: "BAJA",
            },
            diferencias: {
              diferenciaNeta: 10,
            },
            partidasConciliatorias: [],
          },
        ],
      } as MockResponse as Response)
      .mockResolvedValueOnce({
        ok: true,
        json: async () => [
          {
            codigo: "INC-1",
            cuentaContable: "1001",
            mensaje: "Mensaje demo",
            severidad: "BAJA",
          },
        ],
      } as MockResponse as Response);

    render(<App />);
    await userEvent.click(screen.getByRole("button", { name: "Entrar" }));

    expect(
      await screen.findByRole("heading", { name: "Dashboard de Conciliacion" })
    ).toBeInTheDocument();
    expect(screen.getByText("Cuentas procesadas")).toBeInTheDocument();

    await waitFor(() => {
      expect(fetchMock).toHaveBeenCalledWith("/api/dashboard/summary", {
        headers: { Authorization: "Bearer token-demo" },
      });
    });
  });
});
