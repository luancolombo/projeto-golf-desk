import { apiClient } from "./apiClient";
import type { Id, Player, PlayerPayload } from "../types";

const BASE_PATH = "/player";
const DEFAULT_SORT = "fullName,asc";

type PlayerPageParams = {
  page: number;
  size: number;
};

function pageQuery({ page, size }: PlayerPageParams) {
  return `page=${page}&size=${size}&sort=${encodeURIComponent(DEFAULT_SORT)}`;
}

export const playerService = {
  findAll() {
    return apiClient.getPageContent<Player>(BASE_PATH);
  },
  findPage(params: PlayerPageParams) {
    return apiClient.getPage<Player>(`${BASE_PATH}?${pageQuery(params)}`);
  },
  findById(id: Id) {
    return apiClient.get<Player>(`${BASE_PATH}/${id}`);
  },
  searchByName(name: string) {
    return apiClient.getPageContent<Player>(`${BASE_PATH}/search?name=${encodeURIComponent(name)}`);
  },
  searchByNamePage(name: string, params: PlayerPageParams) {
    return apiClient.getPage<Player>(`${BASE_PATH}/search?name=${encodeURIComponent(name)}&${pageQuery(params)}`);
  },
  create(player: PlayerPayload) {
    return apiClient.post<Player, PlayerPayload>(BASE_PATH, player);
  },
  update(player: PlayerPayload) {
    return apiClient.put<Player, PlayerPayload>(BASE_PATH, player);
  },
  remove(id: Id) {
    return apiClient.delete(`${BASE_PATH}/${id}`);
  }
};
