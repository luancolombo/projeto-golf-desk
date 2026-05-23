import type { CashRegisterClosure } from "../types/cashRegister";
import { formatDate } from "./cashRegisterFormat";
import { Button } from "../../../components/ui/button";
import { Lock, Printer } from "lucide-react";

type CashRegisterClosePanelProps = {
  closure: CashRegisterClosure;
  isLoading: boolean;
  mode: "preview" | "closed";
  notes: string;
  onNotesChange: (notes: string) => void;
  onCloseCashRegister: () => void;
  onPrintReport: () => void;
};

export function CashRegisterClosePanel({
  closure,
  isLoading,
  mode,
  notes,
  onNotesChange,
  onCloseCashRegister,
  onPrintReport
}: CashRegisterClosePanelProps) {
  const isClosed = mode === "closed";

  return (
    <section className="grid gap-4 rounded-lg border border-slate-200 bg-slate-50 p-5 lg:grid-cols-[minmax(260px,1fr)_minmax(280px,0.8fr)_auto] lg:items-end">
      <div>
        <p className="mb-2 text-xs font-black uppercase tracking-[0.18em] text-[#2f7d5b]">Acao final</p>
        <h2 className="text-2xl font-black text-slate-950">{isClosed ? "Caixa fechado" : "Fechar caixa"}</h2>
        <p className="mt-2 text-sm leading-6 text-slate-600">
          {isClosed
            ? `Fechamento salvo para ${formatDate(closure.businessDate)}.`
            : `Revise os valores antes de fechar o caixa de ${formatDate(closure.businessDate)}.`}
        </p>
      </div>

      {!isClosed && (
        <label className="grid gap-2">
          <span className="text-sm font-semibold text-slate-600">Observacoes</span>
          <textarea
            className="min-h-24 rounded-md border border-slate-300 bg-white px-3 py-2 text-sm text-slate-950 outline-none focus:border-[#2f7d5b] focus:ring-2 focus:ring-[#2f7d5b]/15"
            maxLength={255}
            rows={3}
            value={notes}
            onChange={(event) => onNotesChange(event.target.value)}
          />
        </label>
      )}

      <div className="flex flex-wrap gap-2">
        <Button className="bg-[#2f7d5b] text-white hover:bg-[#236445]" disabled={isLoading || isClosed} type="button" onClick={onCloseCashRegister}>
          <Lock className="h-4 w-4" />
          {isLoading ? "Fechando..." : "Fechar caixa"}
        </Button>
        <Button className="bg-white text-slate-700 hover:bg-slate-100" disabled={!closure} type="button" variant="outline" onClick={onPrintReport}>
          <Printer className="h-4 w-4" />
          Imprimir relatorio
        </Button>
      </div>
    </section>
  );
}
