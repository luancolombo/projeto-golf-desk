import { type FormEvent, useEffect, useMemo, useState } from "react";
import { Boxes, Edit, List, PackageCheck, RotateCcw, Search, Trash2 } from "lucide-react";
import {
  getApiErrorMessage,
  getApiErrorResponse,
  rentalDamageReportService,
  rentalItemService,
  rentalTransactionService
} from "../api";
import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle
} from "../components/ui/dialog";
import { Input } from "../components/ui/input";
import { useAuth } from "../features/auth/AuthContext";
import { canDeleteRecords, canManageRentalItems } from "../features/auth/permissions";
import type { RentalItem, RentalItemPayload } from "../types";

type FeedbackType = "success" | "error" | "";

type Feedback = {
  message: string;
  type: FeedbackType;
};

type RentalItemFormState = {
  id: string;
  name: string;
  type: string;
  totalStock: string;
  availableStock: string;
  rentalPrice: string;
  active: boolean;
};

type MaterialsPageProps = {
  onApiStatusChange: (status: string) => void;
};

type ReturnNote = {
  createdAt: string;
  reportId?: number;
  returnedCount: number;
  notes: string;
};

const emptyForm: RentalItemFormState = {
  id: "",
  name: "",
  type: "",
  totalStock: "",
  availableStock: "",
  rentalPrice: "",
  active: true
};

function toPayload(form: RentalItemFormState): RentalItemPayload {
  return {
    id: form.id ? Number(form.id) : undefined,
    name: form.name.trim(),
    type: form.type.trim(),
    totalStock: Number(form.totalStock),
    availableStock: form.availableStock ? Number(form.availableStock) : null,
    rentalPrice: Number(form.rentalPrice || 0),
    active: form.active
  };
}

function toForm(rentalItem: RentalItem): RentalItemFormState {
  return {
    id: rentalItem.id ? String(rentalItem.id) : "",
    name: rentalItem.name ?? "",
    type: rentalItem.type ?? "",
    totalStock: rentalItem.totalStock === undefined ? "" : String(rentalItem.totalStock),
    availableStock: rentalItem.availableStock === undefined || rentalItem.availableStock === null ? "" : String(rentalItem.availableStock),
    rentalPrice: rentalItem.rentalPrice === undefined ? "" : String(rentalItem.rentalPrice),
    active: rentalItem.active ?? true
  };
}

function formatJson(value: unknown) {
  return JSON.stringify(value, null, 2);
}

function formatMoney(value: number | null | undefined) {
  return new Intl.NumberFormat("pt-PT", {
    style: "currency",
    currency: "EUR"
  }).format(Number(value || 0));
}

function isActiveRentalItem(rentalItem: RentalItem) {
  return rentalItem.active !== false;
}

export function MaterialsPage({ onApiStatusChange }: MaterialsPageProps) {
  const { role } = useAuth();
  const [rentalItems, setRentalItems] = useState<RentalItem[]>([]);
  const [visibleRentalItems, setVisibleRentalItems] = useState<RentalItem[]>([]);
  const [form, setForm] = useState<RentalItemFormState>(emptyForm);
  const [searchId, setSearchId] = useState("");
  const [rentalItemPendingDelete, setRentalItemPendingDelete] = useState<RentalItem | null>(null);
  const [isReturnAllDialogOpen, setIsReturnAllDialogOpen] = useState(false);
  const [returnNotes, setReturnNotes] = useState("");
  const [lastReturnNote, setLastReturnNote] = useState<ReturnNote | null>(() => {
    const storedNote = window.localStorage.getItem("golf-office-last-material-return-note");
    return storedNote ? JSON.parse(storedNote) as ReturnNote : null;
  });
  const [feedback, setFeedback] = useState<Feedback>({
    message: "Cadastre ou consulte materiais da API.",
    type: "success"
  });
  const [apiStatus, setApiStatus] = useState("Aguardando consulta");
  const [requestJson, setRequestJson] = useState("Nenhuma requisicao enviada ainda.");
  const [responseJson, setResponseJson] = useState("Nenhuma resposta recebida ainda.");
  const [isLoading, setIsLoading] = useState(false);

  const formTitle = form.id ? `Editando material #${form.id}` : "Novo material";
  const rentalItemCountLabel = useMemo(
    () => `${visibleRentalItems.length} material${visibleRentalItems.length === 1 ? "" : "is"}`,
    [visibleRentalItems.length]
  );
  const canManageInventory = canManageRentalItems(role);
  const canDelete = canDeleteRecords(role);

  useEffect(() => {
    onApiStatusChange(apiStatus);
  }, [apiStatus, onApiStatusChange]);

  function showRequest(method: string, url: string, body?: unknown) {
    setRequestJson(formatJson({ method, url, body: body ?? null }));
  }

  function showResponse(data: unknown) {
    setResponseJson(formatJson(data));
  }

  function resetForm() {
    setForm(emptyForm);
  }

  function saveReturnNote(note: ReturnNote) {
    window.localStorage.setItem("golf-office-last-material-return-note", JSON.stringify(note));
    setLastReturnNote(note);
  }

  async function loadRentalItems() {
    setIsLoading(true);
    setFeedback({ message: "Carregando materiais...", type: "success" });
    showRequest("GET", "/rental-item");

    try {
      const data = await rentalItemService.findAll();
      const activeData = data.filter(isActiveRentalItem);

      setRentalItems(data);
      setVisibleRentalItems(activeData);
      showResponse(data);
      setApiStatus("Conectada");
      setFeedback({ message: "Materiais ativos carregados com sucesso.", type: "success" });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setVisibleRentalItems([]);
      setApiStatus("Falha na conexao");
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const payload = toPayload(form);
    const isEditing = Boolean(payload.id);
    const method = isEditing ? "PUT" : "POST";
    showRequest(method, "/rental-item", payload);
    setIsLoading(true);

    try {
      const savedRentalItem = isEditing
        ? await rentalItemService.update(payload)
        : await rentalItemService.create(payload);

      showResponse(savedRentalItem);
      resetForm();
      await loadRentalItems();
      setFeedback({
        message: isEditing ? "Material atualizado com sucesso." : "Material cadastrado com sucesso.",
        type: "success"
      });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  async function handleSearchById() {
    const id = Number(searchId);

    if (!id) {
      setResponseJson("Nenhuma resposta recebida ainda.");
      setFeedback({ message: "Informe um id para buscar.", type: "error" });
      return;
    }

    setIsLoading(true);
    showRequest("GET", `/rental-item/${id}`);

    try {
      const rentalItem = await rentalItemService.findById(id);
      setVisibleRentalItems([rentalItem]);
      showResponse(rentalItem);
      setApiStatus("Conectada");
      setFeedback({
        message: rentalItem.active === false
          ? `Material #${id} encontrado, mas esta desativado.`
          : `Material #${id} encontrado com sucesso.`,
        type: rentalItem.active === false ? "error" : "success"
      });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setVisibleRentalItems([]);
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  async function deleteRentalItem(id: number) {
    setIsLoading(true);
    showRequest("DELETE", `/rental-item/${id}`);

    try {
      await rentalItemService.remove(id);
      showResponse({
        message: "Material removido da operacao. Se tinha historico de rentals, foi desativado para preservar registros."
      });
      resetForm();
      await loadRentalItems();
      setFeedback({
        message: "Material removido da lista operacional. Se tinha historico, foi desativado em vez de apagado fisicamente.",
        type: "success"
      });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  async function confirmDeleteRentalItem() {
    if (!rentalItemPendingDelete?.id) {
      setRentalItemPendingDelete(null);
      return;
    }

    const rentalItemId = rentalItemPendingDelete.id;
    setRentalItemPendingDelete(null);
    await deleteRentalItem(rentalItemId);
  }

  function editRentalItem(rentalItem: RentalItem) {
    setForm(toForm(rentalItem));
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  async function returnAllMaterials() {
    setIsReturnAllDialogOpen(false);
    setIsLoading(true);
    showRequest("PUT", "/rental-transaction/return-all");

    try {
      const returnedTransactions = await rentalTransactionService.returnAll();
      const damageReport = returnNotes.trim()
        ? await rentalDamageReportService.create({
            description: returnNotes.trim(),
            status: "OPEN"
          })
        : null;
      const note = {
        createdAt: new Date().toISOString(),
        reportId: damageReport?.id,
        returnedCount: returnedTransactions.length,
        notes: returnNotes.trim()
      };

      if (damageReport && note.notes) {
        saveReturnNote(note);
      }

      showResponse({
        returnedTransactions,
        damageReport
      });
      setReturnNotes("");
      await loadRentalItems();
      setApiStatus("Conectada");
      setFeedback({
        message: `${returnedTransactions.length} transacao${returnedTransactions.length === 1 ? "" : "es"} de material processada${returnedTransactions.length === 1 ? "" : "s"} para devolucao.`,
        type: "success"
      });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setApiStatus("Falha na conexao");
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <div className="materials-page grid gap-6">
      <section className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
        <div className="mb-5 flex flex-wrap items-start justify-between gap-4">
          <div>
            <p className="mb-2 text-xs font-black uppercase tracking-[0.18em] text-[#2f7d5b]">Fim do dia</p>
            <h2 className="text-2xl font-black text-slate-950">Devolucao de materiais</h2>
            <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-600">
              Devolve todos os materiais alugados ao estoque e registra uma anotacao de avaria quando necessario.
            </p>
          </div>
          <Button
            className="h-11 bg-[#2f7d5b] text-white hover:bg-[#236445]"
            disabled={isLoading}
            type="button"
            onClick={() => setIsReturnAllDialogOpen(true)}
          >
            <PackageCheck className="h-4 w-4" />
            {isLoading ? "Devolvendo..." : "Devolver todos"}
          </Button>
        </div>

        <label className="grid gap-2">
          <span className="text-sm font-semibold text-slate-600">Avarias observadas</span>
          <textarea
            className="min-h-24 rounded-md border border-slate-300 bg-white px-3 py-2 text-sm text-slate-950 outline-none focus:border-[#2f7d5b] focus:ring-2 focus:ring-[#2f7d5b]/15"
            maxLength={300}
            placeholder="Ex.: Buggy #4 voltou com pneu danificado."
            rows={3}
            value={returnNotes}
            onChange={(event) => setReturnNotes(event.target.value)}
          />
        </label>

        {lastReturnNote ? (
          <div className="mt-4 grid gap-1 rounded-lg border border-amber-200 bg-amber-50 p-4">
            <span className="text-xs font-black uppercase tracking-[0.16em] text-amber-700">Ultima anotacao</span>
            <strong className="text-slate-950">
              {new Date(lastReturnNote.createdAt).toLocaleString("pt-PT")}
              {lastReturnNote.reportId ? ` - Report #${lastReturnNote.reportId}` : ""}
            </strong>
            <p className="m-0 text-sm text-slate-700">{lastReturnNote.notes}</p>
          </div>
        ) : null}
      </section>

      <section className={`grid gap-6 ${canManageInventory ? "xl:grid-cols-[minmax(320px,420px)_minmax(0,1fr)]" : ""}`.trim()}>
        {canManageInventory ? (
          <article className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
            <div className="mb-5 flex items-start justify-between gap-4">
              <div>
                <p className="mb-2 text-xs font-black uppercase tracking-[0.18em] text-[#2f7d5b]">Inventario</p>
                <h2 className="text-2xl font-black text-slate-950">{formTitle}</h2>
              </div>
              <Button className="bg-white text-slate-700 hover:bg-slate-100" type="button" variant="outline" onClick={resetForm}>
                <RotateCcw className="h-4 w-4" />
                Limpar
              </Button>
            </div>

            <form className="grid gap-4" onSubmit={handleSubmit}>
              <label className="grid gap-2">
                <span className="text-sm font-semibold text-slate-600">Nome</span>
                <Input
                  className="border-slate-300 bg-white text-slate-950"
                  maxLength={50}
                  required
                  type="text"
                  value={form.name}
                  onChange={(event) => setForm({ ...form, name: event.target.value })}
                />
              </label>

              <label className="grid gap-2">
                <span className="text-sm font-semibold text-slate-600">Tipo</span>
                <Input
                  className="border-slate-300 bg-white text-slate-950"
                  maxLength={50}
                  placeholder="Ex.: BUGGY, TROLLEY, CLUBS"
                  required
                  type="text"
                  value={form.type}
                  onChange={(event) => setForm({ ...form, type: event.target.value })}
                />
              </label>

              <label className="grid gap-2">
                <span className="text-sm font-semibold text-slate-600">Estoque total</span>
                <Input
                  className="border-slate-300 bg-white text-slate-950"
                  min={0}
                  required
                  step={1}
                  type="number"
                  value={form.totalStock}
                  onChange={(event) => setForm({ ...form, totalStock: event.target.value })}
                />
              </label>

              <label className="grid gap-2">
                <span className="text-sm font-semibold text-slate-600">Estoque disponivel</span>
                <Input
                  className="border-slate-300 bg-white text-slate-950"
                  min={0}
                  placeholder="Automatico se vazio"
                  step={1}
                  type="number"
                  value={form.availableStock}
                  onChange={(event) => setForm({ ...form, availableStock: event.target.value })}
                />
              </label>

              <label className="grid gap-2">
                <span className="text-sm font-semibold text-slate-600">Preco de aluguer</span>
                <Input
                  className="border-slate-300 bg-white text-slate-950"
                  min={0}
                  required
                  step={0.01}
                  type="number"
                  value={form.rentalPrice}
                  onChange={(event) => setForm({ ...form, rentalPrice: event.target.value })}
                />
              </label>

              <label className="flex items-center gap-3 rounded-md border border-slate-200 bg-slate-50 px-4 py-3">
                <input
                  checked={form.active}
                  className="h-4 w-4"
                  type="checkbox"
                  onChange={(event) => setForm({ ...form, active: event.target.checked })}
                />
                <span className="text-sm font-semibold text-slate-700">Material ativo</span>
              </label>

              <Button className="h-11 bg-[#2f7d5b] text-white hover:bg-[#236445]" disabled={isLoading} type="submit">
                <Boxes className="h-4 w-4" />
                {isLoading ? "Salvando..." : "Salvar material"}
              </Button>
            </form>
          </article>
        ) : null}

        <article className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
          <div className="mb-5 flex flex-wrap items-start justify-between gap-4">
            <div>
              <p className="mb-2 text-xs font-black uppercase tracking-[0.18em] text-[#2f7d5b]">Estoque</p>
              <h2 className="text-2xl font-black text-slate-950">Materiais cadastrados</h2>
            </div>
            <Badge className="bg-emerald-100 text-emerald-800 hover:bg-emerald-100">
              <Boxes className="mr-1 h-3.5 w-3.5" />
              {rentalItemCountLabel}
            </Badge>
          </div>

          <div className="mb-4 grid gap-3 md:grid-cols-[minmax(160px,260px)_auto_auto]">
            <Input
              className="border-slate-300 bg-white text-slate-950"
              min={1}
              placeholder="Buscar por id"
              type="number"
              value={searchId}
              onChange={(event) => setSearchId(event.target.value)}
              onKeyDown={(event) => {
                if (event.key === "Enter") {
                  void handleSearchById();
                }
              }}
            />
            <Button className="bg-white text-slate-700 hover:bg-slate-100" disabled={isLoading} type="button" variant="outline" onClick={handleSearchById}>
              <Search className="h-4 w-4" />
              Buscar ID
            </Button>
            <Button className="bg-[#052d5f] text-white hover:bg-[#073a73]" disabled={isLoading} type="button" onClick={loadRentalItems}>
              <List className="h-4 w-4" />
              Listar ativos
            </Button>
          </div>

          <p className={`feedback ${feedback.type}`.trim()}>{feedback.message}</p>

          <div className="overflow-x-auto rounded-lg border border-slate-200">
            <table>
              <thead className="bg-slate-50">
                <tr>
                  <th>Material</th>
                  <th>Tipo</th>
                  <th>Estoque</th>
                  <th>Preco</th>
                  <th>Status</th>
                  {canManageInventory || canDelete ? <th>Acoes</th> : null}
                </tr>
              </thead>
              <tbody>
                {visibleRentalItems.length === 0 ? (
                  <tr>
                    <td className="empty-state" colSpan={canManageInventory || canDelete ? 6 : 5}>
                      {isLoading ? "Carregando materiais..." : "Nenhum material encontrado."}
                    </td>
                  </tr>
                ) : (
                  visibleRentalItems.map((rentalItem) => (
                    <tr key={rentalItem.id}>
                      <td>
                        <div className="row-main">{rentalItem.name}</div>
                        <div className="row-sub">ID #{rentalItem.id}</div>
                      </td>
                      <td>{rentalItem.type}</td>
                      <td>
                        <div className="row-main">
                          {rentalItem.availableStock ?? 0}/{rentalItem.totalStock}
                        </div>
                        <div className="row-sub">disponivel/total</div>
                      </td>
                      <td>{formatMoney(rentalItem.rentalPrice)}</td>
                      <td>
                        <Badge className={rentalItem.active ? "bg-emerald-100 text-emerald-800 hover:bg-emerald-100" : "bg-slate-100 text-slate-700 hover:bg-slate-100"}>
                          {rentalItem.active ? "ACTIVE" : "INACTIVE"}
                        </Badge>
                      </td>
                      {canManageInventory || canDelete ? (
                        <td>
                          <div className="flex flex-wrap gap-2">
                            {canManageInventory ? (
                              <Button className="bg-blue-100 text-blue-800 hover:bg-blue-200" size="sm" type="button" onClick={() => editRentalItem(rentalItem)}>
                                <Edit className="h-4 w-4" />
                                Editar
                              </Button>
                            ) : null}
                            {canDelete ? (
                              <Button
                                className="bg-red-100 text-red-800 hover:bg-red-200"
                                size="sm"
                                type="button"
                                onClick={() => setRentalItemPendingDelete(rentalItem)}
                              >
                                <Trash2 className="h-4 w-4" />
                                Excluir
                              </Button>
                            ) : null}
                          </div>
                        </td>
                      ) : null}
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </article>
      </section>

      <section className="json-grid">
        <article className="json-card">
          <p className="json-label">Ultima requisicao</p>
          <pre>{requestJson}</pre>
        </article>
        <article className="json-card">
          <p className="json-label">Ultima resposta</p>
          <pre>{responseJson}</pre>
        </article>
      </section>

      <Dialog open={isReturnAllDialogOpen} onOpenChange={setIsReturnAllDialogOpen}>
        <DialogContent className="border-slate-200 bg-white text-slate-950">
          <DialogHeader>
            <DialogTitle>Devolver todos os materiais?</DialogTitle>
            <DialogDescription className="text-slate-600">
              Todos os materiais alugados serao processados para retorno ao estoque. Se houver texto em avarias, um relatorio sera registrado.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button className="bg-white text-slate-700 hover:bg-slate-100" disabled={isLoading} type="button" variant="outline" onClick={() => setIsReturnAllDialogOpen(false)}>
              Voltar
            </Button>
            <Button
              className="bg-[#2f7d5b] text-white hover:bg-[#236445]"
              disabled={isLoading}
              type="button"
              onClick={() => {
                void returnAllMaterials();
              }}
            >
              Devolver todos
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <Dialog open={Boolean(rentalItemPendingDelete)} onOpenChange={(open) => !open && setRentalItemPendingDelete(null)}>
        <DialogContent className="border-slate-200 bg-white text-slate-950">
          <DialogHeader>
            <DialogTitle>Excluir material?</DialogTitle>
            <DialogDescription className="text-slate-600">
              {rentalItemPendingDelete
                ? `${rentalItemPendingDelete.name} sera removido da operacao. Se tiver historico, o backend deve desativar para preservar registros.`
                : "Confirme para continuar."}
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button className="bg-white text-slate-700 hover:bg-slate-100" disabled={isLoading} type="button" variant="outline" onClick={() => setRentalItemPendingDelete(null)}>
              Voltar
            </Button>
            <Button
              className="bg-red-600 text-white hover:bg-red-700"
              disabled={isLoading}
              type="button"
              onClick={() => {
                void confirmDeleteRentalItem();
              }}
            >
              Excluir
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
