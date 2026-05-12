import { type FormEvent, useMemo, useState } from "react";
import { ApiError, playerService } from "../api";
import type { AppPage } from "../App";
import type { Player, PlayerPayload } from "../types";

type FeedbackType = "success" | "error" | "";

type Feedback = {
  message: string;
  type: FeedbackType;
};

type PlayerFormState = {
  id: string;
  fullName: string;
  taxNumber: string;
  email: string;
  phone: string;
  handCap: string;
  member: boolean;
  notes: string;
};

const emptyForm: PlayerFormState = {
  id: "",
  fullName: "",
  taxNumber: "",
  email: "",
  phone: "",
  handCap: "",
  member: false,
  notes: ""
};

function getErrorMessage(error: unknown) {
  if (error instanceof ApiError) {
    return error.message;
  }

  if (error instanceof Error) {
    return error.message;
  }

  return "Nao foi possivel concluir a operacao.";
}

function toPayload(form: PlayerFormState): PlayerPayload {
  return {
    id: form.id ? Number(form.id) : undefined,
    fullName: form.fullName.trim(),
    taxNumber: form.taxNumber.trim(),
    email: form.email.trim(),
    phone: form.phone.trim(),
    handCap: form.handCap.trim(),
    member: form.member,
    notes: form.notes.trim()
  };
}

function toForm(player: Player): PlayerFormState {
  return {
    id: player.id ? String(player.id) : "",
    fullName: player.fullName ?? "",
    taxNumber: player.taxNumber ?? "",
    email: player.email ?? "",
    phone: player.phone ?? "",
    handCap: player.handCap ?? "",
    member: Boolean(player.member),
    notes: player.notes ?? ""
  };
}

function formatJson(value: unknown) {
  return JSON.stringify(value, null, 2);
}

type PlayersPageProps = {
  onNavigate: (page: AppPage) => void;
};

export function PlayersPage({ onNavigate }: PlayersPageProps) {
  const [players, setPlayers] = useState<Player[]>([]);
  const [visiblePlayers, setVisiblePlayers] = useState<Player[]>([]);
  const [form, setForm] = useState<PlayerFormState>(emptyForm);
  const [searchName, setSearchName] = useState("");
  const [searchId, setSearchId] = useState("");
  const [feedback, setFeedback] = useState<Feedback>({
    message: "Aguardando comando para consultar a API.",
    type: "success"
  });
  const [apiStatus, setApiStatus] = useState("Aguardando consulta");
  const [requestJson, setRequestJson] = useState("Nenhuma requisicao enviada ainda.");
  const [responseJson, setResponseJson] = useState("Nenhuma resposta recebida ainda.");
  const [isLoading, setIsLoading] = useState(false);

  const formTitle = form.id ? `Editando #${form.id}` : "Novo player";
  const playerCountLabel = useMemo(
    () => `${visiblePlayers.length} player${visiblePlayers.length === 1 ? "" : "s"}`,
    [visiblePlayers.length]
  );

  function showRequest(method: string, url: string, body?: unknown) {
    setRequestJson(formatJson({ method, url, body: body ?? null }));
  }

  function showResponse(data: unknown) {
    setResponseJson(formatJson(data));
  }

  function resetForm() {
    setForm(emptyForm);
  }

  async function loadPlayers() {
    setIsLoading(true);
    setFeedback({ message: "Carregando players...", type: "success" });
    showRequest("GET", "/player");

    try {
      const data = await playerService.findAll();
      setPlayers(data);
      setVisiblePlayers(data);
      showResponse(data);
      setApiStatus("Conectada");
      setFeedback({ message: "Players carregados com sucesso.", type: "success" });
    } catch (error) {
      const message = getErrorMessage(error);
      setVisiblePlayers([]);
      setApiStatus("Falha na conexao");
      setFeedback({ message, type: "error" });
      showResponse({ error: message });
    } finally {
      setIsLoading(false);
    }
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const payload = toPayload(form);
    const isEditing = Boolean(payload.id);
    const method = isEditing ? "PUT" : "POST";
    showRequest(method, "/player", payload);
    setIsLoading(true);

    try {
      const savedPlayer = isEditing
        ? await playerService.update(payload)
        : await playerService.create(payload);

      showResponse(savedPlayer);
      resetForm();
      await loadPlayers();
      setFeedback({
        message: isEditing ? "Player atualizado com sucesso." : "Player cadastrado com sucesso.",
        type: "success"
      });
    } catch (error) {
      const message = getErrorMessage(error);
      setFeedback({ message, type: "error" });
      showResponse({ error: message });
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
    showRequest("GET", `/player/${id}`);

    try {
      const player = await playerService.findById(id);
      setVisiblePlayers([player]);
      showResponse(player);
      setApiStatus("Conectada");
      setFeedback({ message: `Player #${id} encontrado com sucesso.`, type: "success" });
    } catch (error) {
      const message = getErrorMessage(error);
      setVisiblePlayers([]);
      setFeedback({ message, type: "error" });
      showResponse({ error: message });
    } finally {
      setIsLoading(false);
    }
  }

  async function handleSearchByName() {
    const name = searchName.trim();

    if (!name) {
      setResponseJson("Nenhuma resposta recebida ainda.");
      setFeedback({ message: "Informe um nome para buscar.", type: "error" });
      return;
    }

    setIsLoading(true);
    showRequest("GET", `/player/search?name=${encodeURIComponent(name)}`);

    try {
      const foundPlayers = await playerService.searchByName(name);
      setPlayers(foundPlayers);
      setVisiblePlayers(foundPlayers);
      showResponse(foundPlayers);
      setApiStatus("Conectada");

      if (foundPlayers.length === 0) {
        setFeedback({ message: `Nenhum player encontrado com o nome "${name}".`, type: "error" });
        return;
      }

      setFeedback({
        message: `${foundPlayers.length} player${foundPlayers.length === 1 ? "" : "s"} encontrado${foundPlayers.length === 1 ? "" : "s"} por nome.`,
        type: "success"
      });
    } catch (error) {
      const message = getErrorMessage(error);
      setVisiblePlayers([]);
      setFeedback({ message, type: "error" });
      showResponse({ error: message });
    } finally {
      setIsLoading(false);
    }
  }

  async function deletePlayer(id: number) {
    const confirmed = window.confirm("Deseja realmente excluir este player?");

    if (!confirmed) {
      return;
    }

    setIsLoading(true);
    showRequest("DELETE", `/player/${id}`);

    try {
      await playerService.remove(id);
      showResponse({ message: `Player ${id} excluido com sucesso.` });
      resetForm();
      await loadPlayers();
      setFeedback({ message: "Player excluido com sucesso.", type: "success" });
    } catch (error) {
      const message = getErrorMessage(error);
      setFeedback({ message, type: "error" });
      showResponse({ error: message });
    } finally {
      setIsLoading(false);
    }
  }

  function editPlayer(player: Player) {
    setForm(toForm(player));
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  return (
    <main className="app-shell">
      <header className="app-header">
        <div>
          <p className="eyebrow">Golf Office</p>
          <h1>Players</h1>
          <p className="page-description">
            Cadastro e consulta de jogadores consumindo diretamente a API Spring Boot.
          </p>
        </div>
        <div className="api-status">
          <span>API</span>
          <strong>{apiStatus}</strong>
        </div>
      </header>

      <section className="entity-tabs" aria-label="Navegacao principal">
        <button className="tab-button active" type="button">
          Players
        </button>
        <button className="tab-button" type="button" disabled>
          Agenda
        </button>
        <button className="tab-button" type="button" onClick={() => onNavigate("materials")}>
          Materiais
        </button>
      </section>

      <section className="content-grid">
        <article className="panel form-panel">
          <div className="panel-header">
            <div>
              <p className="section-tag">Cadastro</p>
              <h2>{formTitle}</h2>
            </div>
            <button className="ghost-button" type="button" onClick={resetForm}>
              Limpar
            </button>
          </div>

          <form className="player-form" onSubmit={handleSubmit}>
            <label>
              <span>Nome completo</span>
              <input
                maxLength={50}
                required
                type="text"
                value={form.fullName}
                onChange={(event) => setForm({ ...form, fullName: event.target.value })}
              />
            </label>

            <label>
              <span>Codigo fiscal</span>
              <input
                maxLength={50}
                required
                type="text"
                value={form.taxNumber}
                onChange={(event) => setForm({ ...form, taxNumber: event.target.value })}
              />
            </label>

            <label>
              <span>Email</span>
              <input
                maxLength={50}
                required
                type="email"
                value={form.email}
                onChange={(event) => setForm({ ...form, email: event.target.value })}
              />
            </label>

            <label>
              <span>Telefone</span>
              <input
                maxLength={50}
                required
                type="text"
                value={form.phone}
                onChange={(event) => setForm({ ...form, phone: event.target.value })}
              />
            </label>

            <label>
              <span>Handicap</span>
              <input
                maxLength={50}
                required
                type="text"
                value={form.handCap}
                onChange={(event) => setForm({ ...form, handCap: event.target.value })}
              />
            </label>

            <label>
              <span>Notas</span>
              <textarea
                maxLength={100}
                required
                rows={4}
                value={form.notes}
                onChange={(event) => setForm({ ...form, notes: event.target.value })}
              />
            </label>

            <label className="checkbox-field">
              <input
                checked={form.member}
                type="checkbox"
                onChange={(event) => setForm({ ...form, member: event.target.checked })}
              />
              <span>Player membro</span>
            </label>

            <div className="form-actions">
              <button className="primary-button" disabled={isLoading} type="submit">
                {isLoading ? "Salvando..." : "Salvar player"}
              </button>
            </div>
          </form>
        </article>

        <article className="panel list-panel">
          <div className="panel-header">
            <div>
              <p className="section-tag">Listagem</p>
              <h2>Players cadastrados</h2>
            </div>
            <span className="count-badge">{playerCountLabel}</span>
          </div>

          <div className="toolbar">
            <input
              placeholder="Buscar por nome"
              type="search"
              value={searchName}
              onChange={(event) => setSearchName(event.target.value)}
              onKeyDown={(event) => {
                if (event.key === "Enter") {
                  void handleSearchByName();
                }
              }}
            />
            <button className="ghost-button" disabled={isLoading} type="button" onClick={handleSearchByName}>
              Buscar nome
            </button>
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
            <button className="ghost-button" disabled={isLoading} type="button" onClick={loadPlayers}>
              Listar todos
            </button>
          </div>

          <p className={`feedback ${feedback.type}`.trim()}>{feedback.message}</p>

          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Nome</th>
                  <th>Fiscal</th>
                  <th>Contato</th>
                  <th>Handicap</th>
                  <th>Membro</th>
                  <th>Acoes</th>
                </tr>
              </thead>
              <tbody>
                {visiblePlayers.length === 0 ? (
                  <tr>
                    <td className="empty-state" colSpan={6}>
                      {isLoading ? "Carregando players..." : "Nenhum player encontrado."}
                    </td>
                  </tr>
                ) : (
                  visiblePlayers.map((player) => (
                    <tr key={player.id}>
                      <td>
                        <div className="row-main">{player.fullName}</div>
                        <div className="row-sub">{player.notes || "Sem observacoes cadastradas."}</div>
                      </td>
                      <td>{player.taxNumber}</td>
                      <td>
                        <div className="row-main">{player.email}</div>
                        <div className="row-sub">{player.phone}</div>
                      </td>
                      <td>{player.handCap || "-"}</td>
                      <td>{player.member ? "Sim" : "Nao"}</td>
                      <td>
                        <div className="table-actions">
                          <button className="action-button edit" type="button" onClick={() => editPlayer(player)}>
                            Editar
                          </button>
                          <button
                            className="action-button delete"
                            type="button"
                            onClick={() => player.id && void deletePlayer(player.id)}
                          >
                            Excluir
                          </button>
                        </div>
                      </td>
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
