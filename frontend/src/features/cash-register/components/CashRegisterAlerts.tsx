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
    <section className="cash-alert-grid" aria-label="Alertas do caixa">
      {alerts.map((alert) => (
        <article className={`cash-alert ${alert.tone}`} key={alert.label}>
          <span>{alert.label}</span>
          <strong>{alert.value}</strong>
        </article>
      ))}
    </section>
  );
}
