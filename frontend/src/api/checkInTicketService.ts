import { apiClient } from "./apiClient";
import type { CheckInTicket, CheckInTicketPayload, Id } from "../types";

const BASE_PATH = "/check-in-ticket";

export const checkInTicketService = {
  findAll() {
    return apiClient.get<CheckInTicket[]>(BASE_PATH);
  },
  findById(id: Id) {
    return apiClient.get<CheckInTicket>(`${BASE_PATH}/${id}`);
  },
  findByBookingPlayerId(bookingPlayerId: Id) {
    return apiClient.get<CheckInTicket[]>(`${BASE_PATH}/booking-player/${bookingPlayerId}`);
  },
  issueByBookingPlayerId(bookingPlayerId: Id) {
    return apiClient.post<CheckInTicket, undefined>(`${BASE_PATH}/booking-player/${bookingPlayerId}/issue`, undefined);
  },
  cancel(id: Id, reason: string) {
    return apiClient.put<CheckInTicket, undefined>(`${BASE_PATH}/${id}/cancel?reason=${encodeURIComponent(reason)}`, undefined);
  },
  create(ticket: CheckInTicketPayload) {
    return apiClient.post<CheckInTicket, CheckInTicketPayload>(BASE_PATH, ticket);
  },
  remove(id: Id) {
    return apiClient.delete(`${BASE_PATH}/${id}`);
  }
};
