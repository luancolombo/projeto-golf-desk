import { apiClient } from "./apiClient";
import type { Id, Receipt, ReceiptPayload } from "../types";

const BASE_PATH = "/receipt";

export const receiptService = {
  findAll() {
    return apiClient.get<Receipt[]>(BASE_PATH);
  },
  findById(id: Id) {
    return apiClient.get<Receipt>(`${BASE_PATH}/${id}`);
  },
  findByBookingId(bookingId: Id) {
    return apiClient.get<Receipt[]>(`${BASE_PATH}/booking/${bookingId}`);
  },
  findByBookingPlayerId(bookingPlayerId: Id) {
    return apiClient.get<Receipt[]>(`${BASE_PATH}/booking-player/${bookingPlayerId}`);
  },
  findByPaymentId(paymentId: Id) {
    return apiClient.get<Receipt[]>(`${BASE_PATH}/payment/${paymentId}`);
  },
  issueByPaymentId(paymentId: Id) {
    return apiClient.post<Receipt, undefined>(`${BASE_PATH}/payment/${paymentId}/issue`, undefined);
  },
  cancel(id: Id, reason: string) {
    return apiClient.put<Receipt, undefined>(`${BASE_PATH}/${id}/cancel?reason=${encodeURIComponent(reason)}`, undefined);
  },
  create(receipt: ReceiptPayload) {
    return apiClient.post<Receipt, ReceiptPayload>(BASE_PATH, receipt);
  },
  update(receipt: ReceiptPayload) {
    return apiClient.put<Receipt, ReceiptPayload>(BASE_PATH, receipt);
  },
  remove(id: Id) {
    return apiClient.delete(`${BASE_PATH}/${id}`);
  }
};
