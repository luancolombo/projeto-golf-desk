import { useEffect, useMemo, useState } from "react";
import { CalendarDays, FileText, Printer, RefreshCw, Search } from "lucide-react";
import { ApiError, getApiErrorMessage, getApiErrorResponse } from "../../../api";
import { Badge } from "../../../components/ui/badge";
import { Button } from "../../../components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle
} from "../../../components/ui/dialog";
import { Input } from "../../../components/ui/input";
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
  onApiStatusChange: (status: string) => void;
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

export function CashRegisterPage({ onApiStatusChange }: CashRegisterPageProps) {
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
  const [isCloseDialogOpen, setIsCloseDialogOpen] = useState(false);

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

    setIsCloseDialogOpen(false);
    const payload = {
      businessDate: closure.businessDate,
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

  useEffect(() => {
    onApiStatusChange(apiStatus);
  }, [apiStatus, onApiStatusChange]);

  return (
    <div className="cash-register-page grid gap-6">
      <section className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
        <div className="mb-5 flex flex-wrap items-start justify-between gap-4">
          <div>
            <p className="mb-2 text-xs font-black uppercase tracking-[0.18em] text-[#2f7d5b]">Fecho diario</p>
            <h2 className="text-2xl font-black text-slate-950">
              {mode === "closed" ? "Fechamento salvo" : "Preview do caixa"}
            </h2>
          </div>
          <Badge className="bg-emerald-100 text-emerald-800 hover:bg-emerald-100">
            <FileText className="mr-1 h-3.5 w-3.5" />
            {itemCountLabel}
          </Badge>
        </div>

        <div className="mb-4 grid gap-3 md:grid-cols-[minmax(190px,260px)_auto_auto]">
          <div className="flex items-center gap-2 rounded-md border border-slate-300 bg-white px-3">
            <CalendarDays className="h-4 w-4 text-slate-500" />
            <Input
              className="h-11 border-0 bg-transparent p-0 text-slate-950 shadow-none focus-visible:ring-0"
              type="date"
              value={selectedDate}
              onChange={(event) => setSelectedDate(event.target.value)}
            />
          </div>
          <Button className="bg-white text-slate-700 hover:bg-slate-100" disabled={isLoading} type="button" variant="outline" onClick={() => void loadCashRegister()}>
            <Search className="h-4 w-4" />
            Buscar caixa
          </Button>
          <Button className="bg-[#052d5f] text-white hover:bg-[#073a73]" disabled={isLoading} type="button" onClick={() => void loadPreview()}>
            <RefreshCw className={isLoading ? "h-4 w-4 animate-spin" : "h-4 w-4"} />
            Atualizar preview
          </Button>
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
              onCloseCashRegister={() => setIsCloseDialogOpen(true)}
              onNotesChange={setNotes}
              onPrintReport={printReport}
            />

            <article className="grid gap-4 rounded-lg border border-slate-200 bg-white p-5">
              <div className="flex flex-wrap items-start justify-between gap-4">
                <div>
                  <p className="mb-2 text-xs font-black uppercase tracking-[0.18em] text-[#2f7d5b]">Itens do caixa</p>
                  <h2 className="text-2xl font-black text-slate-950">Movimentos e alertas</h2>
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

      <Dialog open={isCloseDialogOpen} onOpenChange={setIsCloseDialogOpen}>
        <DialogContent className="border-slate-200 bg-white text-slate-950">
          <DialogHeader>
            <DialogTitle>Fechar caixa?</DialogTitle>
            <DialogDescription className="text-slate-600">
              {closure
                ? `Confirme o fechamento do caixa de ${closure.businessDate}. Depois disso, o fechamento fica salvo para consulta e impressao.`
                : "Carregue um preview antes de fechar o caixa."}
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button className="bg-white text-slate-700 hover:bg-slate-100" disabled={isLoading} type="button" variant="outline" onClick={() => setIsCloseDialogOpen(false)}>
              Voltar
            </Button>
            <Button
              className="bg-[#2f7d5b] text-white hover:bg-[#236445]"
              disabled={isLoading || !closure}
              type="button"
              onClick={() => {
                void closeCashRegister();
              }}
            >
              <Printer className="h-4 w-4" />
              Fechar caixa
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
