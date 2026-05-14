import type { CashRegisterClosure } from "../types/cashRegister";
import { formatDate, formatDateTime, formatMoney, itemTypeLabel } from "./cashRegisterFormat";

type CashRegisterReportPreviewProps = {
  closure: CashRegisterClosure;
  mode: "preview" | "closed";
};

export function CashRegisterReportPreview({ closure, mode }: CashRegisterReportPreviewProps) {
  const items = closure.items || [];

  return (
    <section className="cash-report-paper" id="cash-register-report-print-area">
      <header className="receipt-header">
        <div>
          <p className="receipt-kicker">Golf Office</p>
          <h3>Fecho de caixa</h3>
          <p className="cash-report-date">{formatDate(closure.businessDate)}</p>
        </div>
        <span className="receipt-status">{mode === "closed" ? "FECHADO" : "PREVIEW"}</span>
      </header>

      <div className="receipt-meta">
        <span>Aberto em</span>
        <strong>{formatDateTime(closure.openedAt)}</strong>
        <span>Fechado em</span>
        <strong>{formatDateTime(closure.closedAt)}</strong>
        <span>Pagamentos</span>
        <strong>{closure.paidPaymentsCount || 0}</strong>
        <span>Reembolsos</span>
        <strong>{closure.refundedPaymentsCount || 0}</strong>
      </div>

      <div className="cash-report-totals">
        <div>
          <span>Dinheiro</span>
          <strong>{formatMoney(closure.cashTotal)}</strong>
        </div>
        <div>
          <span>Cartao</span>
          <strong>{formatMoney(closure.cardTotal)}</strong>
        </div>
        <div>
          <span>MB Way</span>
          <strong>{formatMoney(closure.mbwayTotal)}</strong>
        </div>
        <div>
          <span>Transferencia</span>
          <strong>{formatMoney(closure.transferTotal)}</strong>
        </div>
      </div>

      <div className="receipt-total-line">
        <span>Total bruto</span>
        <strong>{formatMoney(closure.grossTotal)}</strong>
      </div>
      <div className="receipt-total-line">
        <span>Reembolsado</span>
        <strong>{formatMoney(closure.refundedTotal)}</strong>
      </div>
      <div className="receipt-total-line grand-total">
        <span>Total liquido</span>
        <strong>{formatMoney(closure.netTotal)}</strong>
      </div>

      <div className="cash-report-items">
        <div className="receipt-item-row header">
          <span>Tipo</span>
          <span>Referencia</span>
          <span>Descricao</span>
          <span>Valor</span>
        </div>
        {items.map((item, index) => (
          <div className="receipt-item-row" key={`${item.type}-${item.referenceId || index}-${item.occurredAt || index}`}>
            <span>{itemTypeLabel(item.type)}</span>
            <span>{item.referenceCode || "-"}</span>
            <span>{item.description || "-"}</span>
            <strong>{formatMoney(item.amount)}</strong>
          </div>
        ))}
      </div>

      {closure.notes && <p className="cash-report-note">{closure.notes}</p>}
    </section>
  );
}
