export type CashRegisterClosureStatus = "OPEN" | "CLOSED" | "CANCELLED" | string;

export type CashRegisterClosureItemType =
  | "PAYMENT"
  | "REFUND"
  | "RECEIPT"
  | "CANCELLED_RECEIPT"
  | "PENDING_BOOKING"
  | "UNRETURNED_RENTAL"
  | string;

export type CashRegisterClosureItem = {
  id?: number;
  cashRegisterClosureId?: number;
  type?: CashRegisterClosureItemType;
  referenceId?: number;
  referenceCode?: string;
  description?: string;
  amount?: number;
  paymentMethod?: string | null;
  paymentStatus?: string | null;
  occurredAt?: string;
};

export type CashRegisterClosure = {
  id?: number;
  businessDate: string;
  openedAt?: string;
  closedAt?: string;
  status?: CashRegisterClosureStatus;
  closedBy?: number | null;
  cashTotal?: number;
  cardTotal?: number;
  mbwayTotal?: number;
  transferTotal?: number;
  grossTotal?: number;
  refundedTotal?: number;
  netTotal?: number;
  paidPaymentsCount?: number;
  refundedPaymentsCount?: number;
  issuedReceiptsCount?: number;
  cancelledReceiptsCount?: number;
  pendingBookingsCount?: number;
  unreturnedRentalsCount?: number;
  notes?: string | null;
  items?: CashRegisterClosureItem[];
};

export type CashRegisterClosurePayload = {
  id?: number;
  businessDate: string;
  closedBy?: number | null;
  notes?: string | null;
};
