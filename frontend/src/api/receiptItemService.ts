import { apiClient } from "./apiClient";
import type { Id, ReceiptItem, ReceiptItemPayload } from "../types";

const BASE_PATH = "/receipt-item";

export const receiptItemService = {
  findAll() {
    return apiClient.get<ReceiptItem[]>(BASE_PATH);
  },
  findById(id: Id) {
    return apiClient.get<ReceiptItem>(`${BASE_PATH}/${id}`);
  },
  findByReceiptId(receiptId: Id) {
    return apiClient.get<ReceiptItem[]>(`${BASE_PATH}/receipt/${receiptId}`);
  },
  create(receiptItem: ReceiptItemPayload) {
    return apiClient.post<ReceiptItem, ReceiptItemPayload>(BASE_PATH, receiptItem);
  },
  update(receiptItem: ReceiptItemPayload) {
    return apiClient.put<ReceiptItem, ReceiptItemPayload>(BASE_PATH, receiptItem);
  },
  remove(id: Id) {
    return apiClient.delete(`${BASE_PATH}/${id}`);
  }
};
