import type { CashRegisterClosure } from "../types/cashRegister";
import { formatDate } from "./cashRegisterFormat";

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
    <section className="cash-close-panel">
      <div>
        <p className="section-tag">Acao final</p>
        <h2>{isClosed ? "Caixa fechado" : "Fechar caixa"}</h2>
        <p className="page-description">
          {isClosed
            ? `Fechamento salvo para ${formatDate(closure.businessDate)}.`
            : `Revise os valores antes de fechar o caixa de ${formatDate(closure.businessDate)}.`}
        </p>
      </div>

      {!isClosed && (
        <label>
          <span>Observacoes</span>
          <textarea
            maxLength={255}
            rows={3}
            value={notes}
            onChange={(event) => onNotesChange(event.target.value)}
          />
        </label>
      )}

      <div className="cash-close-actions">
        <button className="primary-button" disabled={isLoading || isClosed} type="button" onClick={onCloseCashRegister}>
          {isLoading ? "Fechando..." : "Fechar caixa"}
        </button>
        <button className="ghost-button" disabled={!closure} type="button" onClick={onPrintReport}>
          Imprimir relatorio
        </button>
      </div>
    </section>
  );
}
