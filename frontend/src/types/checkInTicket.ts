import type { HateoasResource, Id, LocalDateString, LocalDateTimeString, LocalTimeString } from "./common";

export type CheckInTicket = HateoasResource & {
  id?: Id;
  ticketNumber?: string | null;
  bookingPlayerId: Id;
  playerNameSnapshot?: string | null;
  playerCountSnapshot?: number | null;
  bookingCodeSnapshot?: string | null;
  playDate?: LocalDateString | null;
  startTime?: LocalTimeString | null;
  startingTee?: string | null;
  crossingTee?: string | null;
  crossingTime?: LocalTimeString | null;
  issuedAt?: LocalDateTimeString | null;
  cancelled?: boolean | null;
  cancelledAt?: LocalDateTimeString | null;
  cancellationReason?: string | null;
};

export type CheckInTicketPayload = Omit<CheckInTicket, "_links">;
