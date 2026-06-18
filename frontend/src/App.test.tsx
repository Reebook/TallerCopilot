import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { App } from "./App";

type MockResponse = {
  ok: boolean;
  json: () => Promise<unknown>;
};

const dashboardResponse = {
  totalCuentasProcesadas: 3,
  totalCuentasConciliadas: 2,
  totalCuentasConDiferencia: 1,
  sumatoriaSaldoFinalFuente: 100,
  sumatoriaSaldoFinalConciliado: 90,
  sumatoriaDiferenciaNeta: 10,
};

const cuentasResponse = [
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
  {
    cuentaMayor: {
      cuentaContable: "2002",
      descripcionCuenta: "Bancos",
      codigoBanco: "01",
      codigoSucursal: "001",
      codigoMoneda: "COP",
    },
    saldos: {
      saldoFinalFuente: 200,
      saldoFinalConciliado: 150,
    },
    estadoConciliacion: {
      codigo: "PARCIAL",
      severidad: "MEDIA",
    },
    diferencias: {
      diferenciaNeta: 50,
    },
    partidasConciliatorias: [
      {
        idPartida: "P-1",
        tipo: "AJUSTE",
        estado: "PENDIENTE",
        fechaPartida: "2026-01-10",
        monto: 15,
      },
    ],
  },
];

const incidentesResponse = [
  {
    codigo: "INC-1",
    cuentaContable: "1001",
    mensaje: "Mensaje demo",
    severidad: "BAJA",
  },
];

function mockSuccessfulDashboardLoad(fetchMock: ReturnType<typeof vi.mocked<typeof fetch>>) {
  fetchMock
    .mockResolvedValueOnce({
      ok: true,
      json: async () => ({ token: "token-demo" }),
    } as MockResponse as Response)
    .mockResolvedValueOnce({
      ok: true,
      json: async () => dashboardResponse,
    } as MockResponse as Response)
    .mockResolvedValueOnce({
      ok: true,
      json: async () => cuentasResponse,
    } as MockResponse as Response)
    .mockResolvedValueOnce({
      ok: true,
      json: async () => incidentesResponse,
    } as MockResponse as Response);
}

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
    mockSuccessfulDashboardLoad(fetchMock);

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

  it("permite filtrar, seleccionar cuenta y ver detalle", async () => {
    const fetchMock = vi.mocked(fetch);
    mockSuccessfulDashboardLoad(fetchMock);

    render(<App />);
    await userEvent.click(screen.getByRole("button", { name: "Entrar" }));

    await screen.findByRole("heading", { name: "Dashboard de Conciliacion" });

    await userEvent.selectOptions(screen.getByLabelText("Estado"), "PARCIAL");
    expect(screen.queryByRole("cell", { name: "1001" })).not.toBeInTheDocument();
    expect(screen.getByRole("cell", { name: "2002" })).toBeInTheDocument();

    await userEvent.click(screen.getByRole("cell", { name: "2002" }));
    expect(await screen.findByText("Detalle de Cuenta 2002")).toBeInTheDocument();
    expect(screen.getByText("P-1")).toBeInTheDocument();

    await userEvent.selectOptions(screen.getByLabelText("Estado"), "CONCILIADA");
    await userEvent.click(screen.getByRole("cell", { name: "1001" }));
    expect(await screen.findByText("Detalle de Cuenta 1001")).toBeInTheDocument();
    expect(screen.getByText("No hay partidas conciliatorias para esta cuenta.")).toBeInTheDocument();
  });

  it("construye URL de export con filtros y token", async () => {
    const fetchMock = vi.mocked(fetch);
    const openMock = vi.mocked(open);
    mockSuccessfulDashboardLoad(fetchMock);

    render(<App />);
    await userEvent.click(screen.getByRole("button", { name: "Entrar" }));

    await screen.findByRole("heading", { name: "Dashboard de Conciliacion" });

    await userEvent.selectOptions(screen.getByLabelText("Estado"), "PARCIAL");
    await userEvent.selectOptions(screen.getByLabelText("Severidad"), "MEDIA");
    await userEvent.click(screen.getByRole("button", { name: "Exportar CSV" }));

    expect(openMock).toHaveBeenCalledWith(
      "/api/export/cuentas.csv?estado=PARCIAL&severidad=MEDIA&token=token-demo",
      "_blank"
    );
  });
});
