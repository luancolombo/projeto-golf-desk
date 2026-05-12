import type { HateoasResource, Id } from "./common";

export type RentalTransactionStatus = "RENTED" | "RETURNED" | "LOST" | "DAMAGED" | "CANCELLED";

export type RentalTransaction = HateoasResource & {
  id?: Id;
  bookingId: Id;
  bookingPlayerId: Id;
  rentalItemId: Id;
  quantity: number;
  status?: RentalTransactionStatus | string | null;
  unitPrice?: number | null;
  totalPrice?: number | null;
};

export type RentalTransactionPayload = Omit<RentalTransaction, "_links">;
