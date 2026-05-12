import { apiClient } from "./apiClient";
import type { Id, Payment, PaymentPayload } from "../types";

const BASE_PATH = "/payment";

export const paymentService = {
  findAll() {
    return apiClient.get<Payment[]>(BASE_PATH);
  },
  findById(id: Id) {
    return apiClient.get<Payment>(`${BASE_PATH}/${id}`);
  },
  findByBookingId(bookingId: Id) {
    return apiClient.get<Payment[]>(`${BASE_PATH}/booking/${bookingId}`);
  },
  findByBookingPlayerId(bookingPlayerId: Id) {
    return apiClient.get<Payment[]>(`${BASE_PATH}/booking-player/${bookingPlayerId}`);
  },
  create(payment: PaymentPayload) {
    return apiClient.post<Payment, PaymentPayload>(BASE_PATH, payment);
  },
  update(payment: PaymentPayload) {
    return apiClient.put<Payment, PaymentPayload>(BASE_PATH, payment);
  },
  remove(id: Id) {
    return apiClient.delete(`${BASE_PATH}/${id}`);
  }
};
