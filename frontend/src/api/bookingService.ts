import { apiClient } from "./apiClient";
import type { Booking, BookingPayload, Id } from "../types";

const BASE_PATH = "/booking";

export const bookingService = {
  findAll() {
    return apiClient.get<Booking[]>(BASE_PATH);
  },
  findById(id: Id) {
    return apiClient.get<Booking>(`${BASE_PATH}/${id}`);
  },
  create(booking: BookingPayload) {
    return apiClient.post<Booking, BookingPayload>(BASE_PATH, booking);
  },
  update(booking: BookingPayload) {
    return apiClient.put<Booking, BookingPayload>(BASE_PATH, booking);
  },
  remove(id: Id) {
    return apiClient.delete(`${BASE_PATH}/${id}`);
  }
};
