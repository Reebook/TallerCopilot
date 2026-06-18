import { useCallback, useEffect, useState } from "react";
import { KpiCard } from "./components/KpiCard";

type DashboardSummary = {
  totalCuentasProcesadas: number;
  totalCuentasConciliadas: number;
  totalCuentasConDiferencia: number;
  sumatoriaSaldoFinalFuente: number;
  sumatoriaSaldoFinalConciliado: number;
  sumatoriaDiferenciaNeta: number;
};

type CuentaData = {
  cuentaMayor: {
    cuentaContable: string;
    descripcionCuenta: string;
    codigoBanco: string;
    codigoSucursal: string;
    codigoMoneda: string;
  };
  saldos: {
    saldoFinalFuente: number;
    saldoFinalConciliado: number;
  };
  estadoConciliacion: {
    codigo: string;
    severidad: string;
  };
  diferencias: {
    diferenciaNeta: number;
  };
  partidasConciliatorias: {
    idPartida: string;
    tipo: string;
    estado: string;
    fechaPartida: string;
    monto: number;
  }[];
};

type Incidente = {
  codigo: string;
  cuentaContable: string;
  mensaje: string;
  severidad: string;
};

export function App() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [token, setToken] = useState<string | null>(null);
  const [authError, setAuthError] = useState<string | null>(null);

  const [data, setData] = useState<DashboardSummary | null>(null);
  const [cuentas, setCuentas] = useState<CuentaData[]>([]);
  const [incidentes, setIncidentes] = useState<Incidente[]>([]);
  const [estadoFiltro, setEstadoFiltro] = useState("");
  const [severidadFiltro, setSeveridadFiltro] = useState("");
  const [selectedCuenta, setSelectedCuenta] = useState<CuentaData | null>(null);
  const [loading, setLoading] = useState(true);

  const authFetch = useCallback(
    (url: string) => {
      if (!token) {
        throw new Error("No autenticado");
      }
      return fetch(url, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
    },
    [token]
  );

  useEffect(() => {
    if (!token) {
      return;
    }

    setLoading(true);
    Promise.all([
      authFetch("/api/dashboard/summary").then((response) => response.json()),
      authFetch("/api/cuentas").then((response) => response.json()),
      authFetch("/api/incidentes").then((response) => response.json()),
    ])
      .then(([summary, cuentasResponse, incidentesResponse]) => {
        setData(summary as DashboardSummary);
        setCuentas(cuentasResponse as CuentaData[]);
        setIncidentes(incidentesResponse as Incidente[]);
      })
      .finally(() => setLoading(false));
  }, [authFetch, token]);

  const cuentasFiltradas = cuentas.filter((cuenta) => {
    const cumpleEstado = !estadoFiltro || cuenta.estadoConciliacion.codigo === estadoFiltro;
    const cumpleSeveridad =
      !severidadFiltro || cuenta.estadoConciliacion.severidad === severidadFiltro;
    return cumpleEstado && cumpleSeveridad;
  });

  const distributionByEstado = cuentasFiltradas.reduce<Record<string, number>>((acc, cuenta) => {
    const key = cuenta.estadoConciliacion.codigo;
    acc[key] = (acc[key] ?? 0) + 1;
    return acc;
  }, {});

  const topDiferencias = [...cuentasFiltradas]
    .sort((a, b) => b.diferencias.diferenciaNeta - a.diferencias.diferenciaNeta)
    .slice(0, 5);

  const maxTopDiff =
    topDiferencias.length > 0
      ? Math.max(...topDiferencias.map((item) => item.diferencias.diferenciaNeta))
      : 0;

  const buildQuery = () => {
    const params = new URLSearchParams();
    if (estadoFiltro) {
      params.set("estado", estadoFiltro);
    }
    if (severidadFiltro) {
      params.set("severidad", severidadFiltro);
    }
    return params.toString();
  };

  const downloadExport = (format: "csv" | "json" | "pdf") => {
    if (!token) {
      return;
    }
    const query = buildQuery();
    const params = new URLSearchParams(query);
    params.set("token", token);
    const url = `/api/export/cuentas.${format}?${params.toString()}`;
    window.open(url, "_blank");
  };

  const handleLogin = async () => {
    setAuthError(null);
    const response = await fetch("/api/auth/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ username, password }),
    });

    if (!response.ok) {
      setAuthError("Credenciales inválidas");
      return;
    }

    const authData = (await response.json()) as { token: string };
    setToken(authData.token);
  };

  if (!token) {
    return (
      <main className="container">
        <header>
          <h1>Acceso al Dashboard</h1>
          <p>Inicia sesión para consultar la conciliación.</p>
        </header>

        <section className="panel login-panel">
          <label>
            Usuario
            <input value={username} onChange={(e) => setUsername(e.target.value)} />
          </label>
          <label>
            Contraseña
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
          </label>
          <button type="button" onClick={handleLogin}>
            Entrar
          </button>
          {authError && <p className="error-text">{authError}</p>}
        </section>
      </main>
    );
  }

  return (
    <main className="container">
      <header>
        <h1>Dashboard de Conciliacion</h1>
        <p>Baseline Spring Boot + React para iniciar el taller.</p>
      </header>

      {loading && <p>Cargando resumen...</p>}

      {data && (
        <section className="kpi-grid">
          <KpiCard label="Cuentas procesadas" value={data.totalCuentasProcesadas} />
          <KpiCard label="Cuentas conciliadas" value={data.totalCuentasConciliadas} />
          <KpiCard label="Con diferencia" value={data.totalCuentasConDiferencia} />
          <KpiCard label="Saldo fuente" value={data.sumatoriaSaldoFinalFuente} />
          <KpiCard label="Saldo conciliado" value={data.sumatoriaSaldoFinalConciliado} />
          <KpiCard label="Diferencia neta" value={data.sumatoriaDiferenciaNeta} />
        </section>
      )}

      <section className="panel">
        <h2>Cuentas Contables</h2>
        <div className="filters-row">
          <label>
            Estado
            <select value={estadoFiltro} onChange={(e) => setEstadoFiltro(e.target.value)}>
              <option value="">Todos</option>
              <option value="CONCILIADA">CONCILIADA</option>
              <option value="PARCIAL">PARCIAL</option>
            </select>
          </label>
          <label>
            Severidad
            <select value={severidadFiltro} onChange={(e) => setSeveridadFiltro(e.target.value)}>
              <option value="">Todas</option>
              <option value="BAJA">BAJA</option>
              <option value="MEDIA">MEDIA</option>
              <option value="ALTA">ALTA</option>
            </select>
          </label>
          <div className="actions-group">
            <button type="button" onClick={() => downloadExport("csv")}>
              Exportar CSV
            </button>
            <button type="button" onClick={() => downloadExport("json")}>
              Exportar JSON
            </button>
            <button type="button" onClick={() => downloadExport("pdf")}>
              Exportar PDF
            </button>
          </div>
        </div>
        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Cuenta</th>
                <th>Descripcion</th>
                <th>Estado</th>
                <th>Severidad</th>
                <th>Diferencia Neta</th>
              </tr>
            </thead>
            <tbody>
              {cuentasFiltradas.map((cuenta) => (
                <tr
                  key={cuenta.cuentaMayor.cuentaContable}
                  className={
                    selectedCuenta?.cuentaMayor.cuentaContable === cuenta.cuentaMayor.cuentaContable
                      ? "row-selected"
                      : ""
                  }
                  onClick={() => setSelectedCuenta(cuenta)}
                >
                  <td>{cuenta.cuentaMayor.cuentaContable}</td>
                  <td>{cuenta.cuentaMayor.descripcionCuenta}</td>
                  <td>{cuenta.estadoConciliacion.codigo}</td>
                  <td>{cuenta.estadoConciliacion.severidad}</td>
                  <td>{cuenta.diferencias.diferenciaNeta}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      {selectedCuenta && (
        <section className="panel">
          <h2>Detalle de Cuenta {selectedCuenta.cuentaMayor.cuentaContable}</h2>
          <p>{selectedCuenta.cuentaMayor.descripcionCuenta}</p>
          <div className="detail-grid">
            <div>
              <strong>Saldo Fuente:</strong> {selectedCuenta.saldos.saldoFinalFuente}
            </div>
            <div>
              <strong>Saldo Conciliado:</strong> {selectedCuenta.saldos.saldoFinalConciliado}
            </div>
            <div>
              <strong>Diferencia Neta:</strong> {selectedCuenta.diferencias.diferenciaNeta}
            </div>
            <div>
              <strong>Estado:</strong> {selectedCuenta.estadoConciliacion.codigo}
            </div>
          </div>

          <h3>Partidas Conciliatorias</h3>
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Tipo</th>
                  <th>Estado</th>
                  <th>Fecha</th>
                  <th>Monto</th>
                </tr>
              </thead>
              <tbody>
                {selectedCuenta.partidasConciliatorias.length === 0 && (
                  <tr>
                    <td colSpan={5}>No hay partidas conciliatorias para esta cuenta.</td>
                  </tr>
                )}
                {selectedCuenta.partidasConciliatorias.map((partida) => (
                  <tr key={partida.idPartida}>
                    <td>{partida.idPartida}</td>
                    <td>{partida.tipo}</td>
                    <td>{partida.estado}</td>
                    <td>{partida.fechaPartida}</td>
                    <td>{partida.monto}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      )}

      <section className="panel charts-grid">
        <article className="chart-card">
          <h2>Distribucion por Estado</h2>
          {Object.entries(distributionByEstado).map(([estado, count]) => (
            <div key={estado} className="bar-row">
              <span className="bar-label">{estado}</span>
              <div className="bar-track">
                <div
                  className="bar-fill"
                  style={{
                    width: `${(count / Math.max(cuentasFiltradas.length, 1)) * 100}%`,
                  }}
                />
              </div>
              <span className="bar-value">{count}</span>
            </div>
          ))}
        </article>

        <article className="chart-card">
          <h2>Top Cuentas con Mayor Diferencia</h2>
          {topDiferencias.map((cuenta) => (
            <div key={cuenta.cuentaMayor.cuentaContable} className="bar-row">
              <span className="bar-label">{cuenta.cuentaMayor.cuentaContable}</span>
              <div className="bar-track">
                <div
                  className="bar-fill alt"
                  style={{
                    width: `${maxTopDiff > 0 ? (cuenta.diferencias.diferenciaNeta / maxTopDiff) * 100 : 0}%`,
                  }}
                />
              </div>
              <span className="bar-value">{cuenta.diferencias.diferenciaNeta}</span>
            </div>
          ))}
        </article>
      </section>

      <section className="panel">
        <h2>Incidentes</h2>
        <ul className="incident-list">
          {incidentes.map((incidente) => (
            <li key={incidente.codigo + incidente.cuentaContable}>
              <strong>{incidente.codigo}</strong> - {incidente.cuentaContable} -{" "}
              {incidente.severidad}: {incidente.mensaje}
            </li>
          ))}
        </ul>
      </section>
    </main>
  );
}
