import type { HateoasResource, Id } from "./common";

export type Player = HateoasResource & {
  id?: Id;
  fullName: string;
  taxNumber: string;
  email: string;
  phone: string;
  handCap: string;
  member: boolean;
  notes: string;
};

export type PlayerPayload = Omit<Player, "_links">;
