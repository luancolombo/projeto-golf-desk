import type { HateoasResource, Id } from "./common";

export type RentalItem = HateoasResource & {
  id?: Id;
  name: string;
  type: string;
  totalStock: number;
  availableStock?: number | null;
  rentalPrice: number;
  active?: boolean | null;
};

export type RentalItemPayload = Omit<RentalItem, "_links">;
