import type { CashRegisterClosure } from "../types/cashRegister";
import { formatDate, formatDateTime, formatMoney } from "./cashRegisterFormat";

type CashRegisterSummaryProps = {
  closure: CashRegisterClosure;
  mode: "preview" | "closed";
};

export function CashRegisterSummary({ closure, mode }: CashRegisterSummaryProps) {
  return (
    <section className="cash-summary-grid" aria-label="Resumo do caixa">
      <article>
        <span className="detail-label">Data do caixa</span>
        <strong>{formatDate(closure.businessDate)}</strong>
        <small>{mode === "closed" ? "Fechamento salvo" : "Preview nao salvo"}</small>
      </article>
      <article>
        <span className="detail-label">Status</span>
        <strong>{closure.status || "-"}</strong>
        <small>{closure.closedAt ? formatDateTime(closure.closedAt) : "Aguardando fechamento"}</small>
      </article>
      <article>
        <span className="detail-label">Total bruto</span>
        <strong>{formatMoney(closure.grossTotal)}</strong>
        <small>{closure.paidPaymentsCount || 0} pagamento(s)</small>
      </article>
      <article>
        <span className="detail-label">Total liquido</span>
        <strong>{formatMoney(closure.netTotal)}</strong>
        <small>{closure.refundedPaymentsCount || 0} reembolso(s)</small>
      </article>
    </section>
  );
}
