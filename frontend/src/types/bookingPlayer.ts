import type { HateoasResource, Id } from "./common";

export type BookingPlayer = HateoasResource & {
  id?: Id;
  bookingId: Id;
  playerId: Id;
  greenFeeAmount?: number | null;
  checkedIn: boolean;
};

export type BookingPlayerPayload = Omit<BookingPlayer, "_links">;
