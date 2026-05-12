import type { HateoasResource, Id, LocalDateString, LocalTimeString } from "./common";

export type TeeTimeStatus = "AVAILABLE" | "BOOKED" | "FULL" | "CANCELLED";

export type TeeTime = HateoasResource & {
  id?: Id;
  playDate: LocalDateString;
  startTime: LocalTimeString;
  maxPlayers: number;
  bookedPlayers: number;
  status: TeeTimeStatus | string;
  baseGreenFee: number;
};

export type TeeTimePayload = Omit<TeeTime, "_links">;
