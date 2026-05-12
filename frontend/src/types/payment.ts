import type { HateoasResource, Id, LocalDateTimeString } from "./common";

export type PaymentStatus = "PENDING" | "PAID" | "REFUNDED" | "CANCELLED";
export type PaymentMethod = "CARD" | "CASH" | "MBWAY" | "TRANSFER" | string;

export type Payment = HateoasResource & {
  id?: Id;
  bookingId: Id;
  bookingPlayerId: Id;
  amount: number;
  method: PaymentMethod;
  status?: PaymentStatus | string | null;
  paidAt?: LocalDateTimeString | null;
};

export type PaymentPayload = Omit<Payment, "_links" | "paidAt"> & {
  paidAt?: LocalDateTimeString | null;
};
