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
    <section className="grid gap-3 md:grid-cols-2 xl:grid-cols-5" aria-label="Totais por metodo">
      {totals.map(([label, value]) => (
        <article className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm" key={label}>
          <span className="text-xs font-black uppercase tracking-[0.14em] text-slate-500">{label}</span>
          <strong className="mt-2 block text-lg text-slate-950">{formatMoney(value)}</strong>
        </article>
      ))}
    </section>
  );
}
