import { apiClient } from "./apiClient";
import type { Id, RentalDamageReport, RentalDamageReportPayload } from "../types";

const BASE_PATH = "/rental-damage-report";

export const rentalDamageReportService = {
  findAll() {
    return apiClient.get<RentalDamageReport[]>(BASE_PATH);
  },
  findById(id: Id) {
    return apiClient.get<RentalDamageReport>(`${BASE_PATH}/${id}`);
  },
  findByStatus(status: string) {
    return apiClient.get<RentalDamageReport[]>(`${BASE_PATH}/status/${status}`);
  },
  findByRentalItemId(rentalItemId: Id) {
    return apiClient.get<RentalDamageReport[]>(`${BASE_PATH}/rental-item/${rentalItemId}`);
  },
  findByRentalTransactionId(rentalTransactionId: Id) {
    return apiClient.get<RentalDamageReport[]>(`${BASE_PATH}/rental-transaction/${rentalTransactionId}`);
  },
  create(report: RentalDamageReportPayload) {
    return apiClient.post<RentalDamageReport, RentalDamageReportPayload>(BASE_PATH, report);
  },
  update(report: RentalDamageReportPayload) {
    return apiClient.put<RentalDamageReport, RentalDamageReportPayload>(BASE_PATH, report);
  },
  resolve(id: Id) {
    return apiClient.put<RentalDamageReport, undefined>(`${BASE_PATH}/${id}/resolve`, undefined);
  },
  remove(id: Id) {
    return apiClient.delete(`${BASE_PATH}/${id}`);
  }
};
