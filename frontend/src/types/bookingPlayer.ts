import type { HateoasResource, Id } from "./common";

export type BookingPlayer = HateoasResource & {
  id?: Id;
  bookingId: Id;
  playerId: Id;
  greenFeeAmount?: number | null;
  playerCount?: number | null;
  checkedIn: boolean;
  status?: "ACTIVE" | "REFUNDED" | "CANCELLED" | string;
};

export type BookingPlayerPayload = Omit<BookingPlayer, "_links">;
