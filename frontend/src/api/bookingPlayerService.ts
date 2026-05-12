import { apiClient } from "./apiClient";
import type { BookingPlayer, BookingPlayerPayload, Id } from "../types";

const BASE_PATH = "/booking-player";

export const bookingPlayerService = {
  findAll() {
    return apiClient.get<BookingPlayer[]>(BASE_PATH);
  },
  findById(id: Id) {
    return apiClient.get<BookingPlayer>(`${BASE_PATH}/${id}`);
  },
  create(bookingPlayer: BookingPlayerPayload) {
    return apiClient.post<BookingPlayer, BookingPlayerPayload>(BASE_PATH, bookingPlayer);
  },
  update(bookingPlayer: BookingPlayerPayload) {
    return apiClient.put<BookingPlayer, BookingPlayerPayload>(BASE_PATH, bookingPlayer);
  },
  remove(id: Id) {
    return apiClient.delete(`${BASE_PATH}/${id}`);
  }
};
