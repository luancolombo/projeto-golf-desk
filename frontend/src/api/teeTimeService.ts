import { apiClient } from "./apiClient";
import type { Id, TeeTime, TeeTimePayload } from "../types";

const BASE_PATH = "/tee-time";

export const teeTimeService = {
  findAll() {
    return apiClient.get<TeeTime[]>(BASE_PATH);
  },
  findById(id: Id) {
    return apiClient.get<TeeTime>(`${BASE_PATH}/${id}`);
  },
  create(teeTime: TeeTimePayload) {
    return apiClient.post<TeeTime, TeeTimePayload>(BASE_PATH, teeTime);
  },
  update(teeTime: TeeTimePayload) {
    return apiClient.put<TeeTime, TeeTimePayload>(BASE_PATH, teeTime);
  },
  remove(id: Id) {
    return apiClient.delete(`${BASE_PATH}/${id}`);
  }
};
