import { apiClient } from "./apiClient";
import type { Id, RentalTransaction, RentalTransactionPayload } from "../types";

const BASE_PATH = "/rental-transaction";

export const rentalTransactionService = {
  findAll() {
    return apiClient.get<RentalTransaction[]>(BASE_PATH);
  },
  findById(id: Id) {
    return apiClient.get<RentalTransaction>(`${BASE_PATH}/${id}`);
  },
  findByBookingId(bookingId: Id) {
    return apiClient.get<RentalTransaction[]>(`${BASE_PATH}/booking/${bookingId}`);
  },
  findByBookingPlayerId(bookingPlayerId: Id) {
    return apiClient.get<RentalTransaction[]>(`${BASE_PATH}/booking-player/${bookingPlayerId}`);
  },
  returnAllByBookingId(bookingId: Id) {
    return apiClient.put<RentalTransaction[], undefined>(`${BASE_PATH}/booking/${bookingId}/return-all`, undefined);
  },
  returnAll() {
    return apiClient.put<RentalTransaction[], undefined>(`${BASE_PATH}/return-all`, undefined);
  },
  create(rentalTransaction: RentalTransactionPayload) {
    return apiClient.post<RentalTransaction, RentalTransactionPayload>(BASE_PATH, rentalTransaction);
  },
  update(rentalTransaction: RentalTransactionPayload) {
    return apiClient.put<RentalTransaction, RentalTransactionPayload>(BASE_PATH, rentalTransaction);
  },
  remove(id: Id) {
    return apiClient.delete(`${BASE_PATH}/${id}`);
  }
};
