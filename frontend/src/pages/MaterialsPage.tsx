import { type FormEvent, useMemo, useState } from "react";
import {
  getApiErrorMessage,
  getApiErrorResponse,
  rentalDamageReportService,
  rentalItemService,
  rentalTransactionService
} from "../api";
import type { AppPage } from "../App";
import { useAuth } from "../features/auth/AuthContext";
import { canCloseCashRegister, canDeleteRecords, canManageRentalItems } from "../features/auth/permissions";
import { SessionBadge } from "../features/auth/SessionBadge";
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
  onNavigate: (page: AppPage) => void;
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

export function MaterialsPage({ onNavigate }: MaterialsPageProps) {
  const { role } = useAuth();
  const [rentalItems, setRentalItems] = useState<RentalItem[]>([]);
  const [visibleRentalItems, setVisibleRentalItems] = useState<RentalItem[]>([]);
  const [form, setForm] = useState<RentalItemFormState>(emptyForm);
  const [searchId, setSearchId] = useState("");
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
  const canViewCashRegister = canCloseCashRegister(role);

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
      setRentalItems(data);
      setVisibleRentalItems(data);
      showResponse(data);
      setApiStatus("Conectada");
      setFeedback({ message: "Materiais carregados com sucesso.", type: "success" });
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
      setFeedback({ message: `Material #${id} encontrado com sucesso.`, type: "success" });
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
    const confirmed = window.confirm("Deseja realmente excluir este material?");

    if (!confirmed) {
      return;
    }

    setIsLoading(true);
    showRequest("DELETE", `/rental-item/${id}`);

    try {
      await rentalItemService.remove(id);
      showResponse({ message: `Material ${id} excluido com sucesso.` });
      resetForm();
      await loadRentalItems();
      setFeedback({ message: "Material excluido com sucesso.", type: "success" });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  function editRentalItem(rentalItem: RentalItem) {
    setForm(toForm(rentalItem));
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  async function returnAllMaterials() {
    const confirmed = window.confirm("Confirmar devolucao de todos os materiais alugados ao estoque?");

    if (!confirmed) {
      return;
    }

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
    <main className="app-shell">
      <header className="app-header">
        <div>
          <p className="eyebrow">Golf Office</p>
          <h1>Materiais</h1>
          <p className="page-description">
            Controle de estoque, preco e disponibilidade dos materiais alugaveis consumindo a API Spring Boot.
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
        <button className="tab-button active" type="button">
          Materiais
        </button>
        {canViewCashRegister ? (
          <button className="tab-button" type="button" onClick={() => onNavigate("cash-register")}>
            Caixa
          </button>
        ) : null}
      </section>

      <section className="panel material-return-panel">
        <div className="panel-header">
          <div>
            <p className="section-tag">Fim do dia</p>
            <h2>Devolucao de materiais</h2>
          </div>
          <button className="primary-button" disabled={isLoading} type="button" onClick={() => void returnAllMaterials()}>
            {isLoading ? "Devolvendo..." : "Devolver todos ao estoque"}
          </button>
        </div>

        <label className="material-return-notes">
          <span>Avarias observadas</span>
          <textarea
            maxLength={300}
            placeholder="Ex.: Buggy #4 voltou com pneu danificado."
            rows={3}
            value={returnNotes}
            onChange={(event) => setReturnNotes(event.target.value)}
          />
        </label>

        {lastReturnNote ? (
          <div className="return-note-preview">
            <span>Ultima anotacao</span>
            <strong>
              {new Date(lastReturnNote.createdAt).toLocaleString("pt-PT")}
              {lastReturnNote.reportId ? ` - Report #${lastReturnNote.reportId}` : ""}
            </strong>
            <p>{lastReturnNote.notes}</p>
          </div>
        ) : null}
      </section>

      <section className={`content-grid ${canManageInventory ? "" : "single-column"}`.trim()}>
        {canManageInventory ? (
          <article className="panel form-panel">
            <div className="panel-header">
              <div>
                <p className="section-tag">Inventario</p>
                <h2>{formTitle}</h2>
              </div>
              <button className="ghost-button" type="button" onClick={resetForm}>
                Limpar
              </button>
            </div>

            <form className="player-form" onSubmit={handleSubmit}>
              <label>
                <span>Nome</span>
                <input
                  maxLength={50}
                  required
                  type="text"
                  value={form.name}
                  onChange={(event) => setForm({ ...form, name: event.target.value })}
                />
              </label>

              <label>
                <span>Tipo</span>
                <input
                  maxLength={50}
                  placeholder="Ex.: BUGGY, TROLLEY, CLUBS"
                  required
                  type="text"
                  value={form.type}
                  onChange={(event) => setForm({ ...form, type: event.target.value })}
                />
              </label>

              <label>
                <span>Estoque total</span>
                <input
                  min={0}
                  required
                  step={1}
                  type="number"
                  value={form.totalStock}
                  onChange={(event) => setForm({ ...form, totalStock: event.target.value })}
                />
              </label>

              <label>
                <span>Estoque disponivel</span>
                <input
                  min={0}
                  placeholder="Automatico se vazio"
                  step={1}
                  type="number"
                  value={form.availableStock}
                  onChange={(event) => setForm({ ...form, availableStock: event.target.value })}
                />
              </label>

              <label>
                <span>Preco de aluguer</span>
                <input
                  min={0}
                  required
                  step={0.01}
                  type="number"
                  value={form.rentalPrice}
                  onChange={(event) => setForm({ ...form, rentalPrice: event.target.value })}
                />
              </label>

              <label className="checkbox-field">
                <input
                  checked={form.active}
                  type="checkbox"
                  onChange={(event) => setForm({ ...form, active: event.target.checked })}
                />
                <span>Material ativo</span>
              </label>

              <div className="form-actions">
                <button className="primary-button" disabled={isLoading} type="submit">
                  {isLoading ? "Salvando..." : "Salvar material"}
                </button>
              </div>
            </form>
          </article>
        ) : null}

        <article className="panel list-panel">
          <div className="panel-header">
            <div>
              <p className="section-tag">Estoque</p>
              <h2>Materiais cadastrados</h2>
            </div>
            <span className="count-badge">{rentalItemCountLabel}</span>
          </div>

          <div className="toolbar">
            <input
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
            <button className="ghost-button" disabled={isLoading} type="button" onClick={handleSearchById}>
              Buscar ID
            </button>
            <button className="ghost-button" disabled={isLoading} type="button" onClick={loadRentalItems}>
              Listar materiais
            </button>
          </div>

          <p className={`feedback ${feedback.type}`.trim()}>{feedback.message}</p>

          <div className="table-wrap">
            <table>
              <thead>
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
                        <span className="status-pill">{rentalItem.active ? "ACTIVE" : "INACTIVE"}</span>
                      </td>
                      {canManageInventory || canDelete ? (
                        <td>
                          <div className="table-actions">
                            {canManageInventory ? (
                              <button className="action-button edit" type="button" onClick={() => editRentalItem(rentalItem)}>
                                Editar
                              </button>
                            ) : null}
                            {canDelete ? (
                              <button
                                className="action-button delete"
                                type="button"
                                onClick={() => rentalItem.id && void deleteRentalItem(rentalItem.id)}
                              >
                                Excluir
                              </button>
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
    </main>
  );
}
