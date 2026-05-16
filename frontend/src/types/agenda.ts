import type { Booking } from "./booking";
import type { BookingPlayer } from "./bookingPlayer";
import type { CheckInTicket } from "./checkInTicket";
import type { LocalDateString } from "./common";
import type { Payment } from "./payment";
import type { Player } from "./player";
import type { Receipt } from "./receipt";
import type { ReceiptItem } from "./receiptItem";
import type { RentalItem } from "./rentalItem";
import type { RentalTransaction } from "./rentalTransaction";
import type { TeeTime } from "./teeTime";

export type AgendaDay = {
  date: LocalDateString;
  players: Player[];
  rentalItems: RentalItem[];
  teeTimes: TeeTime[];
  bookings: Booking[];
  bookingPlayers: BookingPlayer[];
  rentalTransactions: RentalTransaction[];
  payments: Payment[];
  receipts: Receipt[];
  receiptItems: ReceiptItem[];
  checkInTickets: CheckInTicket[];
};
