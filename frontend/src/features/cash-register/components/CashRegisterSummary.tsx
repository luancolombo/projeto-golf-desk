import type { CashRegisterClosure } from "../types/cashRegister";
import { formatDate, formatDateTime, formatMoney } from "./cashRegisterFormat";
import { Badge } from "../../../components/ui/badge";

type CashRegisterSummaryProps = {
  closure: CashRegisterClosure;
  mode: "preview" | "closed";
};

export function CashRegisterSummary({ closure, mode }: CashRegisterSummaryProps) {
  return (
    <section className="grid gap-3 md:grid-cols-2 xl:grid-cols-4" aria-label="Resumo do caixa">
      <article className="rounded-lg border border-slate-200 bg-slate-50 p-4">
        <span className="text-xs font-black uppercase tracking-[0.14em] text-slate-500">Data do caixa</span>
        <strong className="mt-2 block text-xl text-slate-950">{formatDate(closure.businessDate)}</strong>
        <Badge className={mode === "closed" ? "mt-2 bg-emerald-100 text-emerald-800 hover:bg-emerald-100" : "mt-2 bg-blue-100 text-blue-800 hover:bg-blue-100"}>
          {mode === "closed" ? "Fechamento salvo" : "Preview nao salvo"}
        </Badge>
      </article>
      <article className="rounded-lg border border-slate-200 bg-slate-50 p-4">
        <span className="text-xs font-black uppercase tracking-[0.14em] text-slate-500">Status</span>
        <strong className="mt-2 block text-xl text-slate-950">{closure.status || "-"}</strong>
        <small className="mt-2 block text-sm text-slate-600">{closure.closedAt ? formatDateTime(closure.closedAt) : "Aguardando fechamento"}</small>
      </article>
      <article className="rounded-lg border border-slate-200 bg-slate-50 p-4">
        <span className="text-xs font-black uppercase tracking-[0.14em] text-slate-500">Total bruto</span>
        <strong className="mt-2 block text-xl text-slate-950">{formatMoney(closure.grossTotal)}</strong>
        <small className="mt-2 block text-sm text-slate-600">{closure.paidPaymentsCount || 0} pagamento(s)</small>
      </article>
      <article className="rounded-lg border border-slate-200 bg-slate-50 p-4">
        <span className="text-xs font-black uppercase tracking-[0.14em] text-slate-500">Total liquido</span>
        <strong className="mt-2 block text-xl text-slate-950">{formatMoney(closure.netTotal)}</strong>
        <small className="mt-2 block text-sm text-slate-600">{closure.refundedPaymentsCount || 0} reembolso(s)</small>
      </article>
    </section>
  );
}
