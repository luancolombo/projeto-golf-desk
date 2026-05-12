import { apiClient } from "./apiClient";
import type { Id, Player, PlayerPayload } from "../types";

const BASE_PATH = "/player";

export const playerService = {
  findAll() {
    return apiClient.get<Player[]>(BASE_PATH);
  },
  findById(id: Id) {
    return apiClient.get<Player>(`${BASE_PATH}/${id}`);
  },
  searchByName(name: string) {
    return apiClient.get<Player[]>(`${BASE_PATH}/search?name=${encodeURIComponent(name)}`);
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
