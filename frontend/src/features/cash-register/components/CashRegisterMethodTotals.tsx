import type { CashRegisterClosure } from "../types/cashRegister";
import { formatMoney } from "./cashRegisterFormat";

type CashRegisterMethodTotalsProps = {
  closure: CashRegisterClosure;
};

export function CashRegisterMethodTotals({ closure }: CashRegisterMethodTotalsProps) {
  const totals = [
    ["Dinheiro", closure.cashTotal],
    ["Cartao", closure.cardTotal],
    ["MB Way", closure.mbwayTotal],
    ["Transferencia", closure.transferTotal],
    ["Reembolsado", closure.refundedTotal]
  ] as const;

  return (
    <section className="cash-method-grid" aria-label="Totais por metodo">
      {totals.map(([label, value]) => (
        <article key={label}>
          <span>{label}</span>
          <strong>{formatMoney(value)}</strong>
        </article>
      ))}
    </section>
  );
}
