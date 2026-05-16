import { apiClient } from "./apiClient";
import type { AgendaDay } from "../types";

const BASE_PATH = "/agenda";

export const agendaService = {
  findDay(date: string) {
    return apiClient.get<AgendaDay>(`${BASE_PATH}/day?date=${encodeURIComponent(date)}`);
  }
};
