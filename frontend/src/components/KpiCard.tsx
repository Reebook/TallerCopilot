type KpiCardProps = {
  label: string;
  value: number;
};

export function KpiCard({ label, value }: KpiCardProps) {
  return (
    <article className="kpi-card">
      <span className="kpi-label">{label}</span>
      <strong className="kpi-value">{new Intl.NumberFormat("es-CO").format(value)}</strong>
    </article>
  );
}
