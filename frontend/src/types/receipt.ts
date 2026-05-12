import type { HateoasResource, Id, LocalDateString, LocalDateTimeString, LocalTimeString } from "./common";

export type Receipt = HateoasResource & {
  id?: Id;
  receiptNumber?: string | null;
  bookingId: Id;
  bookingPlayerId: Id;
  paymentId: Id;
  playerNameSnapshot?: string | null;
  playerTaxNumberSnapshot?: string | null;
  bookingCodeSnapshot?: string | null;
  playDate?: LocalDateString | null;
  startTime?: LocalTimeString | null;
  greenFeeAmount?: number | null;
  rentalAmount?: number | null;
  totalAmount?: number | null;
  paymentMethod?: string | null;
  paymentStatus?: string | null;
  issuedAt?: LocalDateTimeString | null;
  cancelled?: boolean | null;
  cancelledAt?: LocalDateTimeString | null;
  cancellationReason?: string | null;
};

export type ReceiptPayload = Omit<Receipt, "_links">;
