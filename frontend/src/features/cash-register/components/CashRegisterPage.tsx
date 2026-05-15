import { useEffect, useMemo, useState } from "react";
import { ApiError, getApiErrorMessage, getApiErrorResponse } from "../../../api";
import type { AppPage } from "../../../App";
import { SessionBadge } from "../../auth/SessionBadge";
import { cashRegisterService } from "../services/cashRegisterService";
import type { CashRegisterClosure } from "../types/cashRegister";
import { CashRegisterAlerts } from "./CashRegisterAlerts";
import { CashRegisterClosePanel } from "./CashRegisterClosePanel";
import { CashRegisterItemsTable } from "./CashRegisterItemsTable";
import { CashRegisterMethodTotals } from "./CashRegisterMethodTotals";
import { CashRegisterReportPreview } from "./CashRegisterReportPreview";
import { CashRegisterSummary } from "./CashRegisterSummary";

type FeedbackType = "success" | "error" | "";

type Feedback = {
  message: string;
  type: FeedbackType;
};

type CashRegisterPageProps = {
  onNavigate: (page: AppPage) => void;
};

type CashRegisterMode = "preview" | "closed";

function todayInputValue() {
  const now = new Date();
  const timezoneOffset = now.getTimezoneOffset() * 60000;
  return new Date(now.getTime() - timezoneOffset).toISOString().slice(0, 10);
}

function formatJson(value: unknown) {
  return JSON.stringify(value, null, 2);
}

export function CashRegisterPage({ onNavigate }: CashRegisterPageProps) {
  const [selectedDate, setSelectedDate] = useState(todayInputValue());
  const [closure, setClosure] = useState<CashRegisterClosure | null>(null);
  const [mode, setMode] = useState<CashRegisterMode>("preview");
  const [notes, setNotes] = useState("");
  const [feedback, setFeedback] = useState<Feedback>({
    message: "Carregue o preview ou fechamento salvo do caixa.",
    type: "success"
  });
  const [apiStatus, setApiStatus] = useState("Aguardando consulta");
  const [requestJson, setRequestJson] = useState("Nenhuma requisicao enviada ainda.");
  const [responseJson, setResponseJson] = useState("Nenhuma resposta recebida ainda.");
  const [isLoading, setIsLoading] = useState(false);

  const itemCountLabel = useMemo(() => {
    const count = closure?.items?.length || 0;
    return `${count} item${count === 1 ? "" : "s"}`;
  }, [closure]);

  function showRequest(method: string, url: string, body?: unknown) {
    setRequestJson(formatJson({ method, url, body: body ?? null }));
  }

  function showResponse(data: unknown) {
    setResponseJson(formatJson(data));
  }

  async function loadCashRegister(date = selectedDate) {
    if (!date) {
      setFeedback({ message: "Informe uma data para consultar o caixa.", type: "error" });
      return;
    }

    setIsLoading(true);
    setFeedback({ message: "Carregando caixa...", type: "success" });
    showRequest("GET", `/cash-register-closure/date/${date}`);

    try {
      const closedClosure = await cashRegisterService.findByDate(date);
      setClosure(closedClosure);
      setMode("closed");
      setNotes(closedClosure.notes || "");
      setApiStatus("Conectada");
      showResponse(closedClosure);
      setFeedback({ message: "Fechamento salvo carregado com sucesso.", type: "success" });
    } catch (error) {
      if (error instanceof ApiError && error.status === 404) {
        await loadPreview(date);
        return;
      }

      const message = getApiErrorMessage(error);
      setClosure(null);
      setApiStatus("Falha na conexao");
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  async function loadPreview(date = selectedDate) {
    setIsLoading(true);
    showRequest("GET", `/cash-register-closure/preview?date=${date}`);

    try {
      const preview = await cashRegisterService.preview(date);
      setClosure(preview);
      setMode("preview");
      setNotes(preview.notes || "");
      setApiStatus("Conectada");
      showResponse(preview);
      setFeedback({ message: "Preview do caixa carregado com sucesso.", type: "success" });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setClosure(null);
      setApiStatus("Falha na conexao");
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  async function closeCashRegister() {
    if (!closure) {
      setFeedback({ message: "Carregue um preview antes de fechar o caixa.", type: "error" });
      return;
    }

    const confirmed = window.confirm(`Confirmar fechamento do caixa de ${closure.businessDate}?`);

    if (!confirmed) {
      return;
    }

    const payload = {
      businessDate: closure.businessDate,
      closedBy: null,
      notes: notes.trim() || null
    };

    setIsLoading(true);
    showRequest("POST", "/cash-register-closure/close", payload);

    try {
      const closedClosure = await cashRegisterService.close(payload);
      setClosure(closedClosure);
      setMode("closed");
      setNotes(closedClosure.notes || "");
      setApiStatus("Conectada");
      showResponse(closedClosure);
      setFeedback({ message: "Caixa fechado com sucesso.", type: "success" });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  function printReport() {
    window.print();
  }

  useEffect(() => {
    void loadCashRegister(selectedDate);
  }, []);

  return (
    <main className="app-shell cash-register-page">
      <header className="app-header">
        <div>
          <p className="eyebrow">Golf Office</p>
          <h1>Caixa</h1>
          <p className="page-description">
            Conferencia, fechamento e relatorio diario do caixa consumindo a API Spring Boot.
          </p>
        </div>
        <SessionBadge apiStatus={apiStatus} />
      </header>

      <section className="entity-tabs" aria-label="Navegacao principal">
        <button className="tab-button" type="button" onClick={() => onNavigate("players")}>
          Players
        </button>
        <button className="tab-button" type="button" onClick={() => onNavigate("agenda")}>
          Agenda
        </button>
        <button className="tab-button" type="button" onClick={() => onNavigate("materials")}>
          Materiais
        </button>
        <button className="tab-button active" type="button">
          Caixa
        </button>
      </section>

      <section className="panel cash-register-panel">
        <div className="panel-header">
          <div>
            <p className="section-tag">Fecho diario</p>
            <h2>{mode === "closed" ? "Fechamento salvo" : "Preview do caixa"}</h2>
          </div>
          <span className="count-badge">{itemCountLabel}</span>
        </div>

        <div className="toolbar cash-toolbar">
          <input
            type="date"
            value={selectedDate}
            onChange={(event) => setSelectedDate(event.target.value)}
          />
          <button className="ghost-button" disabled={isLoading} type="button" onClick={() => void loadCashRegister()}>
            Buscar caixa
          </button>
          <button className="ghost-button" disabled={isLoading} type="button" onClick={() => void loadPreview()}>
            Atualizar preview
          </button>
        </div>

        <p className={`feedback ${feedback.type}`.trim()}>{feedback.message}</p>

        {!closure ? (
          <p className="empty-state">Nenhum dado de caixa carregado.</p>
        ) : (
          <div className="cash-register-content">
            <CashRegisterSummary closure={closure} mode={mode} />
            <CashRegisterMethodTotals closure={closure} />
            <CashRegisterAlerts closure={closure} />

            <CashRegisterClosePanel
              closure={closure}
              isLoading={isLoading}
              mode={mode}
              notes={notes}
              onCloseCashRegister={closeCashRegister}
              onNotesChange={setNotes}
              onPrintReport={printReport}
            />

            <article className="cash-items-section">
              <div className="panel-header">
                <div>
                  <p className="section-tag">Itens do caixa</p>
                  <h2>Movimentos e alertas</h2>
                </div>
              </div>
              <CashRegisterItemsTable items={closure.items || []} />
            </article>

            <CashRegisterReportPreview closure={{ ...closure, notes }} mode={mode} />
          </div>
        )}
      </section>

      <section className="json-grid no-print">
        <article className="json-card">
          <p className="json-label">Ultima requisicao</p>
          <pre>{requestJson}</pre>
        </article>
        <article className="json-card">
          <p className="json-label">Ultima resposta</p>
          <pre>{responseJson}</pre>
        </article>
      </section>
    </main>
  );
}
