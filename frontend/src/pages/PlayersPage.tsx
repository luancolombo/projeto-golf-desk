import { type FormEvent, useEffect, useMemo, useState } from "react";
import { ChevronLeft, ChevronRight, Edit, List, RotateCcw, Search, Trash2, UserPlus, Users } from "lucide-react";
import { getApiErrorMessage, getApiErrorResponse, playerService } from "../api";
import type { PageResponse } from "../api/apiClient";
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
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle
} from "../components/ui/sheet";
import { useAuth } from "../features/auth/AuthContext";
import { canDeleteRecords } from "../features/auth/permissions";
import type { Player, PlayerPayload } from "../types";

type FeedbackType = "success" | "error" | "";

type Feedback = {
  message: string;
  type: FeedbackType;
};

type MemberFilter = "all" | "members" | "nonMembers";
type PlayerListMode = "list" | "name" | "id";

type PlayerPageState = {
  page: number;
  size: number;
  totalPages: number;
  totalElements: number;
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

const PLAYER_PAGE_SIZE = 20;
const initialPageState: PlayerPageState = {
  page: 0,
  size: PLAYER_PAGE_SIZE,
  totalPages: 0,
  totalElements: 0
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

function getPageState<T>(pageResponse: PageResponse<T>): PlayerPageState {
  const page = pageResponse.page;

  return {
    page: page?.number ?? pageResponse.number ?? 0,
    size: page?.size ?? pageResponse.size ?? PLAYER_PAGE_SIZE,
    totalPages: page?.totalPages ?? pageResponse.totalPages ?? 0,
    totalElements: page?.totalElements ?? pageResponse.totalElements ?? pageResponse.content.length
  };
}

type PlayersPageProps = {
  onApiStatusChange: (status: string) => void;
};

export function PlayersPage({ onApiStatusChange }: PlayersPageProps) {
  const { role } = useAuth();
  const [players, setPlayers] = useState<Player[]>([]);
  const [visiblePlayers, setVisiblePlayers] = useState<Player[]>([]);
  const [playerPage, setPlayerPage] = useState<PlayerPageState>(initialPageState);
  const [listMode, setListMode] = useState<PlayerListMode>("list");
  const [memberFilter, setMemberFilter] = useState<MemberFilter>("all");
  const [form, setForm] = useState<PlayerFormState>(emptyForm);
  const [searchName, setSearchName] = useState("");
  const [searchId, setSearchId] = useState("");
  const [playerPendingDelete, setPlayerPendingDelete] = useState<Player | null>(null);
  const [isPlayerFormOpen, setIsPlayerFormOpen] = useState(false);
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
    () => `${playerPage.totalElements} player${playerPage.totalElements === 1 ? "" : "s"}`,
    [playerPage.totalElements]
  );
  const hasPaginatedResult = listMode !== "id" && playerPage.totalPages > 0;
  const currentPageLabel = hasPaginatedResult ? playerPage.page + 1 : 0;
  const canGoPrevious = hasPaginatedResult && playerPage.page > 0 && !isLoading;
  const canGoNext = hasPaginatedResult && playerPage.page + 1 < playerPage.totalPages && !isLoading;
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

  function applyPlayerPage(pageResponse: PageResponse<Player>) {
    const content = pageResponse.content;
    setPlayers(content);
    setVisiblePlayers(content);
    setPlayerPage(getPageState(pageResponse));
    showResponse(pageResponse);
    setApiStatus("Conectada");
    return content;
  }

  async function loadPlayers(page = 0) {
    setIsLoading(true);
    setFeedback({ message: "Carregando players...", type: "success" });
    setListMode("list");
    showRequest("GET", `/player?page=${page}&size=${PLAYER_PAGE_SIZE}&sort=fullName,asc`);

    try {
      const data = await playerService.findPage({ page, size: PLAYER_PAGE_SIZE });
      applyPlayerPage(data);
      setFeedback({ message: "Players carregados com sucesso.", type: "success" });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setPlayers([]);
      setVisiblePlayers([]);
      setPlayerPage(initialPageState);
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
      setIsPlayerFormOpen(false);
      await reloadCurrentPlayers();
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
      setPlayers([player]);
      setVisiblePlayers([player]);
      setPlayerPage({ page: 0, size: 1, totalPages: 1, totalElements: 1 });
      setListMode("id");
      showResponse(player);
      setApiStatus("Conectada");
      setFeedback({ message: `Player #${id} encontrado com sucesso.`, type: "success" });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setPlayers([]);
      setVisiblePlayers([]);
      setPlayerPage(initialPageState);
      setListMode("id");
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  async function loadPlayersByName(name: string, page = 0) {
    setIsLoading(true);
    setFeedback({ message: "Buscando players por nome...", type: "success" });
    setListMode("name");
    showRequest("GET", `/player/search?name=${encodeURIComponent(name)}&page=${page}&size=${PLAYER_PAGE_SIZE}&sort=fullName,asc`);

    try {
      const data = await playerService.searchByNamePage(name, { page, size: PLAYER_PAGE_SIZE });
      const foundPlayers = applyPlayerPage(data);

      if (foundPlayers.length === 0) {
        setFeedback({ message: `Nenhum player encontrado com o nome "${name}".`, type: "error" });
        return;
      }

      setFeedback({
        message: `${data.page?.totalElements ?? data.totalElements ?? foundPlayers.length} player${(data.page?.totalElements ?? data.totalElements ?? foundPlayers.length) === 1 ? "" : "s"} encontrado${(data.page?.totalElements ?? data.totalElements ?? foundPlayers.length) === 1 ? "" : "s"} por nome.`,
        type: "success"
      });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setPlayers([]);
      setVisiblePlayers([]);
      setPlayerPage(initialPageState);
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

    await loadPlayersByName(name, 0);
  }

  async function deletePlayer(id: number) {
    setIsLoading(true);
    showRequest("DELETE", `/player/${id}`);

    try {
      await playerService.remove(id);
      showResponse({ message: `Player ${id} excluido com sucesso.` });
      resetForm();
      await reloadCurrentPlayers();
      setFeedback({ message: "Player excluido com sucesso.", type: "success" });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  async function reloadCurrentPlayers() {
    if (listMode === "name" && searchName.trim()) {
      await loadPlayersByName(searchName.trim(), playerPage.page);
      return;
    }

    await loadPlayers(playerPage.page);
  }

  async function goToPlayerPage(page: number) {
    if (page < 0 || page >= playerPage.totalPages) {
      return;
    }

    if (listMode === "name" && searchName.trim()) {
      await loadPlayersByName(searchName.trim(), page);
      return;
    }

    await loadPlayers(page);
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
    setIsPlayerFormOpen(true);
  }

  function openNewPlayerForm() {
    resetForm();
    setIsPlayerFormOpen(true);
  }

  return (
    <div className="players-page grid gap-6">
      <section className="grid gap-6">
        <article className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
          <div className="mb-5 flex flex-wrap items-start justify-between gap-4">
            <div>
              <p className="mb-2 text-xs font-black uppercase tracking-[0.18em] text-[#2f7d5b]">Listagem</p>
              <h2 className="text-2xl font-black text-slate-950">Players cadastrados</h2>
            </div>
            <div className="flex flex-wrap items-center gap-3">
              <Badge className="bg-emerald-100 text-emerald-800 hover:bg-emerald-100">
                <Users className="mr-1 h-3.5 w-3.5" />
                {playerCountLabel}
              </Badge>
              <Button className="bg-[#2f7d5b] text-white hover:bg-[#236445]" type="button" onClick={openNewPlayerForm}>
                <UserPlus className="h-4 w-4" />
                Novo player
              </Button>
            </div>
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
            <Button
              className="bg-[#052d5f] text-white hover:bg-[#073a73]"
              disabled={isLoading}
              type="button"
              onClick={() => {
                void loadPlayers(0);
              }}
            >
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

          {hasPaginatedResult ? (
            <div className="mt-4 flex flex-wrap items-center justify-between gap-3 border-t border-slate-100 pt-4">
              <p className="text-sm font-semibold text-slate-600">
                Pagina {currentPageLabel} de {playerPage.totalPages} - {playerPage.totalElements} players
              </p>
              <div className="flex items-center gap-2">
                <Button
                  className="bg-white text-slate-700 hover:bg-slate-100"
                  disabled={!canGoPrevious}
                  type="button"
                  variant="outline"
                  onClick={() => {
                    void goToPlayerPage(playerPage.page - 1);
                  }}
                >
                  <ChevronLeft className="h-4 w-4" />
                  Anterior
                </Button>
                <Button
                  className="bg-white text-slate-700 hover:bg-slate-100"
                  disabled={!canGoNext}
                  type="button"
                  variant="outline"
                  onClick={() => {
                    void goToPlayerPage(playerPage.page + 1);
                  }}
                >
                  Proxima
                  <ChevronRight className="h-4 w-4" />
                </Button>
              </div>
            </div>
          ) : null}
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

      <Sheet
        open={isPlayerFormOpen}
        onOpenChange={(open) => {
          setIsPlayerFormOpen(open);
          if (!open) {
            resetForm();
          }
        }}
      >
        <SheetContent className="w-full overflow-y-auto border-slate-200 bg-white text-slate-950 sm:max-w-md">
          <SheetHeader className="mb-6 pr-8">
            <p className="mb-0 text-xs font-black uppercase tracking-[0.18em] text-[#2f7d5b]">Cadastro</p>
            <SheetTitle className="text-2xl font-black text-slate-950">{formTitle}</SheetTitle>
            <SheetDescription className="text-slate-600">
              Preencha os dados do jogador e salve para atualizar a listagem.
            </SheetDescription>
          </SheetHeader>

          <form className="grid gap-4" onSubmit={handleSubmit}>
            <div className="flex justify-end">
              <Button className="bg-white text-slate-700 hover:bg-slate-100" type="button" variant="outline" onClick={resetForm}>
                <RotateCcw className="h-4 w-4" />
                Limpar
              </Button>
            </div>

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
        </SheetContent>
      </Sheet>

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
