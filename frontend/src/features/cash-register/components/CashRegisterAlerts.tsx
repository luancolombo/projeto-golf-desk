import type { CashRegisterClosure } from "../types/cashRegister";

type CashRegisterAlertsProps = {
  closure: CashRegisterClosure;
};

export function CashRegisterAlerts({ closure }: CashRegisterAlertsProps) {
  const alerts = [
    {
      label: "Bookings pendentes",
      value: closure.pendingBookingsCount || 0,
      tone: (closure.pendingBookingsCount || 0) > 0 ? "warning" : "ok"
    },
    {
      label: "Materiais nao devolvidos",
      value: closure.unreturnedRentalsCount || 0,
      tone: (closure.unreturnedRentalsCount || 0) > 0 ? "warning" : "ok"
    },
    {
      label: "Recibos cancelados",
      value: closure.cancelledReceiptsCount || 0,
      tone: (closure.cancelledReceiptsCount || 0) > 0 ? "danger" : "ok"
    },
    {
      label: "Recibos emitidos",
      value: closure.issuedReceiptsCount || 0,
      tone: "ok"
    }
  ];

  return (
    <section className="grid gap-3 md:grid-cols-2 xl:grid-cols-4" aria-label="Alertas do caixa">
      {alerts.map((alert) => (
        <article
          className={[
            "rounded-lg border p-4",
            alert.tone === "ok" ? "border-emerald-200 bg-emerald-50" : "",
            alert.tone === "warning" ? "border-amber-200 bg-amber-50" : "",
            alert.tone === "danger" ? "border-red-200 bg-red-50" : "",
          ].join(" ")}
          key={alert.label}
        >
          <span className="text-xs font-black uppercase tracking-[0.14em] text-slate-500">{alert.label}</span>
          <strong className="mt-2 block text-xl text-slate-950">{alert.value}</strong>
        </article>
      ))}
    </section>
  );
}
