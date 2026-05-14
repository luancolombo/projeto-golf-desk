import { apiClient } from "../../../api/apiClient";
import type { CashRegisterClosure, CashRegisterClosurePayload } from "../types/cashRegister";

export const cashRegisterService = {
  findAll() {
    return apiClient.get<CashRegisterClosure[]>("/cash-register-closure");
  },

  findById(id: number) {
    return apiClient.get<CashRegisterClosure>(`/cash-register-closure/${id}`);
  },

  findByDate(date: string) {
    return apiClient.get<CashRegisterClosure>(`/cash-register-closure/date/${date}`);
  },

  preview(date: string) {
    return apiClient.get<CashRegisterClosure>(`/cash-register-closure/preview?date=${encodeURIComponent(date)}`);
  },

  close(payload: CashRegisterClosurePayload) {
    return apiClient.post<CashRegisterClosure, CashRegisterClosurePayload>("/cash-register-closure/close", payload);
  },

  cancel(id: number) {
    return apiClient.delete(`/cash-register-closure/${id}`);
  }
};
