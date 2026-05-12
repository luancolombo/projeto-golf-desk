import type { HateoasResource, Id } from "./common";

export type ReceiptItem = HateoasResource & {
  id?: Id;
  receiptId: Id;
  description: string;
  itemType?: string | null;
  referenceId?: Id | null;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
};

export type ReceiptItemPayload = Omit<ReceiptItem, "_links">;
