export type RentalDamageReportStatus = "OPEN" | "RESOLVED" | "CANCELLED" | string;

export type RentalDamageReport = {
  id?: number;
  rentalTransactionId?: number | null;
  rentalItemId?: number | null;
  description: string;
  status?: RentalDamageReportStatus;
  reportedAt?: string;
  resolvedAt?: string | null;
  reportedBy?: number | null;
  resolvedBy?: number | null;
};

export type RentalDamageReportPayload = {
  id?: number;
  rentalTransactionId?: number | null;
  rentalItemId?: number | null;
  description: string;
  status?: RentalDamageReportStatus;
  reportedBy?: number | null;
  resolvedBy?: number | null;
};
