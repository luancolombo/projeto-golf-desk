import type { UserRole } from "../../types";

export function isManager(role: UserRole | null | undefined) {
  return role === "MANAGER";
}

export function canDeleteRecords(role: UserRole | null | undefined) {
  return isManager(role);
}

export function canManageRentalItems(role: UserRole | null | undefined) {
  return isManager(role);
}

export function canCloseCashRegister(role: UserRole | null | undefined) {
  return isManager(role);
}
