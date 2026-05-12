import { apiClient } from "./apiClient";
import type { Id, RentalItem, RentalItemPayload } from "../types";

const BASE_PATH = "/rental-item";

export const rentalItemService = {
  findAll() {
    return apiClient.get<RentalItem[]>(BASE_PATH);
  },
  findById(id: Id) {
    return apiClient.get<RentalItem>(`${BASE_PATH}/${id}`);
  },
  create(rentalItem: RentalItemPayload) {
    return apiClient.post<RentalItem, RentalItemPayload>(BASE_PATH, rentalItem);
  },
  update(rentalItem: RentalItemPayload) {
    return apiClient.put<RentalItem, RentalItemPayload>(BASE_PATH, rentalItem);
  },
  remove(id: Id) {
    return apiClient.delete(`${BASE_PATH}/${id}`);
  }
};
