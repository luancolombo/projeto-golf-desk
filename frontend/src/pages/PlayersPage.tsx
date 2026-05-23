import { type FormEvent, useEffect, useMemo, useState } from "react";
import { Edit, List, RotateCcw, Search, Trash2, UserPlus, Users } from "lucide-react";
import { getApiErrorMessage, getApiErrorResponse, playerService } from "../api";
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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from "../components/ui/select";
import { useAuth } from "../features/auth/AuthContext";
import { canDeleteRecords } from "../features/auth/permissions";
import type { Player, PlayerPayload } from "../types";

type FeedbackType = "success" | "error" | "";

type Feedback = {
  message: string;
  type: FeedbackType;
};

type MemberFilter = "all" | "members" | "nonMembers";

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
  onApiStatusChange: (status: string) => void;
};

export function PlayersPage({ onApiStatusChange }: PlayersPageProps) {
  const { role } = useAuth();
  const [players, setPlayers] = useState<Player[]>([]);
  const [visiblePlayers, setVisiblePlayers] = useState<Player[]>([]);
  const [memberFilter, setMemberFilter] = useState<MemberFilter>("all");
  const [form, setForm] = useState<PlayerFormState>(emptyForm);
  const [searchName, setSearchName] = useState("");
  const [searchId, setSearchId] = useState("");
  const [playerPendingDelete, setPlayerPendingDelete] = useState<Player | null>(null);
  const [feedback, setFeedback] = useState<Feedback>({
    message: "Aguardando comando para consultar a API.",
    type: "success"
  });
  const [apiStatus, setApiStatus] = useState("Aguardando consulta");
  const [requestJson, setRequestJson] = useState("Nenhuma requisicao enviada ainda.");
  const [responseJson, setResponseJson] = useState("Nenhuma resposta recebida ainda.");
  const [isLoading, setIsLoading] = useState(false);

  const formTitle = form.id ? `Editando #${form.id}` : "Novo player";
  const filteredPlayers = useMemo(() => {
    if (memberFilter === "members") {
      return visiblePlayers.filter((player) => player.member);
    }

    if (memberFilter === "nonMembers") {
      return visiblePlayers.filter((player) => !player.member);
    }

    return visiblePlayers;
  }, [memberFilter, visiblePlayers]);
  const playerCountLabel = useMemo(
    () => `${filteredPlayers.length} player${filteredPlayers.length === 1 ? "" : "s"}`,
    [filteredPlayers.length]
  );
  const canDelete = canDeleteRecords(role);

  useEffect(() => {
    onApiStatusChange(apiStatus);
  }, [apiStatus, onApiStatusChange]);

  function getEmptyMessage() {
    if (isLoading) {
      return "Carregando players...";
    }

    if (visiblePlayers.length > 0 && filteredPlayers.length === 0) {
      return "Nenhum player encontrado para este filtro.";
    }

    return "Nenhum player encontrado.";
  }

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
      const message = getApiErrorMessage(error);
      setVisiblePlayers([]);
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
    showRequest("GET", `/player/${id}`);

    try {
      const player = await playerService.findById(id);
      setVisiblePlayers([player]);
      showResponse(player);
      setApiStatus("Conectada");
      setFeedback({ message: `Player #${id} encontrado com sucesso.`, type: "success" });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setVisiblePlayers([]);
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
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
      const message = getApiErrorMessage(error);
      setVisiblePlayers([]);
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  async function deletePlayer(id: number) {
    setIsLoading(true);
    showRequest("DELETE", `/player/${id}`);

    try {
      await playerService.remove(id);
      showResponse({ message: `Player ${id} excluido com sucesso.` });
      resetForm();
      await loadPlayers();
      setFeedback({ message: "Player excluido com sucesso.", type: "success" });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  async function confirmDeletePlayer() {
    if (!playerPendingDelete?.id) {
      setPlayerPendingDelete(null);
      return;
    }

    const playerId = playerPendingDelete.id;
    setPlayerPendingDelete(null);
    await deletePlayer(playerId);
  }

  function editPlayer(player: Player) {
    setForm(toForm(player));
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  return (
    <div className="players-page grid gap-6">
      <section className="grid gap-6 xl:grid-cols-[minmax(320px,420px)_minmax(0,1fr)]">
        <article className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
          <div className="mb-5 flex items-start justify-between gap-4">
            <div>
              <p className="mb-2 text-xs font-black uppercase tracking-[0.18em] text-[#2f7d5b]">Cadastro</p>
              <h2 className="text-2xl font-black text-slate-950">{formTitle}</h2>
            </div>
            <Button className="bg-white text-slate-700 hover:bg-slate-100" type="button" variant="outline" onClick={resetForm}>
              <RotateCcw className="h-4 w-4" />
              Limpar
            </Button>
          </div>

          <form className="grid gap-4" onSubmit={handleSubmit}>
            <label className="grid gap-2">
              <span className="text-sm font-semibold text-slate-600">Nome completo</span>
              <Input
                className="border-slate-300 bg-white text-slate-950"
                maxLength={50}
                required
                type="text"
                value={form.fullName}
                onChange={(event) => setForm({ ...form, fullName: event.target.value })}
              />
            </label>

            <label className="grid gap-2">
              <span className="text-sm font-semibold text-slate-600">Codigo fiscal</span>
              <Input
                className="border-slate-300 bg-white text-slate-950"
                maxLength={50}
                required
                type="text"
                value={form.taxNumber}
                onChange={(event) => setForm({ ...form, taxNumber: event.target.value })}
              />
            </label>

            <label className="grid gap-2">
              <span className="text-sm font-semibold text-slate-600">Email</span>
              <Input
                className="border-slate-300 bg-white text-slate-950"
                maxLength={50}
                required
                type="email"
                value={form.email}
                onChange={(event) => setForm({ ...form, email: event.target.value })}
              />
            </label>

            <label className="grid gap-2">
              <span className="text-sm font-semibold text-slate-600">Telefone</span>
              <Input
                className="border-slate-300 bg-white text-slate-950"
                maxLength={50}
                required
                type="text"
                value={form.phone}
                onChange={(event) => setForm({ ...form, phone: event.target.value })}
              />
            </label>

            <label className="grid gap-2">
              <span className="text-sm font-semibold text-slate-600">Handicap</span>
              <Input
                className="border-slate-300 bg-white text-slate-950"
                maxLength={50}
                required
                type="text"
                value={form.handCap}
                onChange={(event) => setForm({ ...form, handCap: event.target.value })}
              />
            </label>

            <label className="grid gap-2">
              <span className="text-sm font-semibold text-slate-600">Notas</span>
              <textarea
                className="min-h-28 rounded-md border border-slate-300 bg-white px-3 py-2 text-sm text-slate-950 outline-none focus:border-[#2f7d5b] focus:ring-2 focus:ring-[#2f7d5b]/15"
                maxLength={100}
                required
                rows={4}
                value={form.notes}
                onChange={(event) => setForm({ ...form, notes: event.target.value })}
              />
            </label>

            <label className="flex items-center gap-3 rounded-md border border-slate-200 bg-slate-50 px-4 py-3">
              <input
                checked={form.member}
                className="h-4 w-4"
                type="checkbox"
                onChange={(event) => setForm({ ...form, member: event.target.checked })}
              />
              <span className="text-sm font-semibold text-slate-700">Player membro</span>
            </label>

            <Button className="h-11 bg-[#2f7d5b] text-white hover:bg-[#236445]" disabled={isLoading} type="submit">
              <UserPlus className="h-4 w-4" />
              {isLoading ? "Salvando..." : "Salvar player"}
            </Button>
          </form>
        </article>

        <article className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
          <div className="mb-5 flex flex-wrap items-start justify-between gap-4">
            <div>
              <p className="mb-2 text-xs font-black uppercase tracking-[0.18em] text-[#2f7d5b]">Listagem</p>
              <h2 className="text-2xl font-black text-slate-950">Players cadastrados</h2>
            </div>
            <Badge className="bg-emerald-100 text-emerald-800 hover:bg-emerald-100">
              <Users className="mr-1 h-3.5 w-3.5" />
              {playerCountLabel}
            </Badge>
          </div>

          <div className="mb-4 grid gap-3 lg:grid-cols-[minmax(180px,1fr)_auto_minmax(150px,0.7fr)_auto_auto_180px]">
            <Input
              className="border-slate-300 bg-white text-slate-950"
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
            <Button className="bg-white text-slate-700 hover:bg-slate-100" disabled={isLoading} type="button" variant="outline" onClick={handleSearchByName}>
              <Search className="h-4 w-4" />
              Nome
            </Button>
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
              ID
            </Button>
            <Button className="bg-[#052d5f] text-white hover:bg-[#073a73]" disabled={isLoading} type="button" onClick={loadPlayers}>
              <List className="h-4 w-4" />
              Listar
            </Button>
            <Select value={memberFilter} onValueChange={(value) => setMemberFilter(value as MemberFilter)}>
              <SelectTrigger className="border-slate-300 bg-white text-slate-950">
                <SelectValue placeholder="Filtro" />
              </SelectTrigger>
              <SelectContent className="border-slate-200 bg-white text-slate-950">
                <SelectItem value="all">Todos</SelectItem>
                <SelectItem value="members">Membros</SelectItem>
                <SelectItem value="nonMembers">Nao membros</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <p className={`feedback ${feedback.type}`.trim()}>{feedback.message}</p>

          <div className="overflow-x-auto rounded-lg border border-slate-200">
            <table>
              <thead className="bg-slate-50">
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
                {filteredPlayers.length === 0 ? (
                  <tr>
                    <td className="empty-state" colSpan={6}>
                      {getEmptyMessage()}
                    </td>
                  </tr>
                ) : (
                  filteredPlayers.map((player) => (
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
                      <td>
                        <Badge className={player.member ? "bg-emerald-100 text-emerald-800 hover:bg-emerald-100" : "bg-slate-100 text-slate-700 hover:bg-slate-100"}>
                          {player.member ? "Membro" : "Nao membro"}
                        </Badge>
                      </td>
                      <td>
                        <div className="flex flex-wrap gap-2">
                          <Button className="bg-blue-100 text-blue-800 hover:bg-blue-200" size="sm" type="button" onClick={() => editPlayer(player)}>
                            <Edit className="h-4 w-4" />
                            Editar
                          </Button>
                          {canDelete ? (
                            <Button
                              className="bg-red-100 text-red-800 hover:bg-red-200"
                              size="sm"
                              type="button"
                              onClick={() => setPlayerPendingDelete(player)}
                            >
                              <Trash2 className="h-4 w-4" />
                              Excluir
                            </Button>
                          ) : null}
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

      <Dialog open={Boolean(playerPendingDelete)} onOpenChange={(open) => !open && setPlayerPendingDelete(null)}>
        <DialogContent className="border-slate-200 bg-white text-slate-950">
          <DialogHeader>
            <DialogTitle>Excluir player?</DialogTitle>
            <DialogDescription className="text-slate-600">
              {playerPendingDelete
                ? `${playerPendingDelete.fullName} sera removido se o backend permitir a exclusao.`
                : "Confirme para continuar."}
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button className="bg-white text-slate-700 hover:bg-slate-100" disabled={isLoading} type="button" variant="outline" onClick={() => setPlayerPendingDelete(null)}>
              Voltar
            </Button>
            <Button
              className="bg-red-600 text-white hover:bg-red-700"
              disabled={isLoading}
              type="button"
              onClick={() => {
                void confirmDeletePlayer();
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
