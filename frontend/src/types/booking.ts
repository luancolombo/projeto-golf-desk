import type { HateoasResource, Id, LocalDateTimeString } from "./common";

export type BookingStatus = "CREATED" | "CONFIRMED" | "CANCELLED";

export type Booking = HateoasResource & {
  id?: Id;
  code?: string | null;
  createdAt?: LocalDateTimeString | null;
  status: BookingStatus | string;
  totalAmount: number;
  createdBy?: Id | null;
  teeTimeId: Id;
};

export type BookingPayload = Omit<Booking, "_links" | "createdAt">;
