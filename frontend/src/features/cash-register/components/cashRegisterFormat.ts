import type { CashRegisterClosureItem } from "../types/cashRegister";

export function formatMoney(value: number | null | undefined) {
  return new Intl.NumberFormat("pt-PT", {
    style: "currency",
    currency: "EUR"
  }).format(Number(value || 0));
}

export function formatDate(value: string | null | undefined) {
  if (!value) {
    return "-";
  }

  return new Intl.DateTimeFormat("pt-PT").format(new Date(`${value}T00:00:00`));
}

export function formatDateTime(value: string | null | undefined) {
  if (!value) {
    return "-";
  }

  return new Intl.DateTimeFormat("pt-PT", {
    dateStyle: "short",
    timeStyle: "short"
  }).format(new Date(value));
}

export function itemTypeLabel(type: CashRegisterClosureItem["type"]) {
  const labels: Record<string, string> = {
    PAYMENT: "Pagamento",
    REFUND: "Reembolso",
    RECEIPT: "Recibo emitido",
    CANCELLED_RECEIPT: "Recibo cancelado",
    PENDING_BOOKING: "Booking pendente",
    UNRETURNED_RENTAL: "Material nao devolvido"
  };

  return type ? labels[type] ?? type : "-";
}

export function signedMoneyClass(value: number | null | undefined) {
  const amount = Number(value || 0);

  if (amount < 0) {
    return "negative";
  }

  if (amount > 0) {
    return "positive";
  }

  return "";
}
