import { useEffect, useMemo, useState, type FormEvent } from "react";
import { CalendarDays, RefreshCw } from "lucide-react";
import {
  agendaService,
  bookingPlayerService,
  bookingService,
  checkInTicketService,
  getApiErrorMessage,
  getApiErrorResponse,
  paymentService,
  receiptService,
  rentalDamageReportService,
  rentalTransactionService,
  teeTimeService
} from "../api";
import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle
} from "../components/ui/dialog";
import { Input } from "../components/ui/input";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle
} from "../components/ui/sheet";
import { useAuth } from "../features/auth/AuthContext";
import { canCancelReceipts, canDeleteRecords, canManageFinancialAdjustments } from "../features/auth/permissions";
import type { AgendaDay, Booking, BookingPlayer, CheckInTicket, Payment, Player, Receipt, ReceiptItem, RentalItem, RentalTransaction, TeeTime } from "../types";

type FeedbackType = "success" | "error" | "";

type Feedback = {
  message: string;
  type: FeedbackType;
};

type AgendaPageProps = {
  onApiStatusChange: (status: string) => void;
};

type AgendaSlot = {
  time: string;
  teeTime?: TeeTime;
  bookings: Booking[];
};

type BookingDetailTab = "summary" | "players" | "rentals" | "payments" | "receipts";

type TeeSheetBookingCell = {
  booking: Booking;
  bookingPlayer: BookingPlayer;
  count: number;
  label: string;
  meta: string;
  rentalIcons: TeeSheetRentalIcon[];
  state: "partial" | "checked-in" | "paid";
};

type TeeSheetRentalIcon = {
  key: string;
  label: string;
  type: "buggy" | "trolley" | "clubs" | "other";
};

type ConfirmationState = {
  title: string;
  description: string;
  confirmLabel: string;
  variant?: "default" | "destructive";
  action: () => Promise<void> | void;
};

function formatJson(value: unknown) {
  return JSON.stringify(value, null, 2);
}

function todayIsoDate() {
  return new Date().toISOString().slice(0, 10);
}

function formatDate(value: string) {
  const [year, month, day] = value.split("-");

  if (!year || !month || !day) {
    return value;
  }

  return `${day}/${month}/${year}`;
}

function formatTime(value: string | undefined) {
  if (!value) {
    return "";
  }

  return value.slice(0, 5);
}

function formatDateTime(value: string | null | undefined) {
  if (!value) {
    return "Sem data";
  }

  return value.replace("T", " ").slice(0, 16);
}

function formatMoney(value: number | null | undefined) {
  return new Intl.NumberFormat("pt-PT", {
    style: "currency",
    currency: "EUR"
  }).format(Number(value || 0));
}

function generateTimeSlots() {
  const slots: string[] = [];
  const start = 7 * 60;
  const end = 19 * 60;

  for (let minutes = start; minutes <= end; minutes += 10) {
    const hour = Math.floor(minutes / 60);
    const minute = minutes % 60;
    slots.push(`${String(hour).padStart(2, "0")}:${String(minute).padStart(2, "0")}`);
  }

  return slots;
}

function getSlotClass(slot: AgendaSlot) {
  if (!slot.teeTime) {
    return "free";
  }

  if (String(slot.teeTime.status).toUpperCase() === "CANCELLED") {
    return "cancelled";
  }

  if (slot.teeTime.bookedPlayers >= slot.teeTime.maxPlayers) {
    return "full";
  }

  if (slot.teeTime.bookedPlayers > 0 || slot.bookings.length > 0) {
    return "partial";
  }

  return "open";
}

function getSlotLabel(slot: AgendaSlot) {
  if (!slot.teeTime) {
    return "Livre";
  }

  if (String(slot.teeTime.status).toUpperCase() === "CANCELLED") {
    return "Cancelado";
  }

  if (slot.teeTime.bookedPlayers >= slot.teeTime.maxPlayers) {
    return "Cheio";
  }

  if (slot.teeTime.bookedPlayers > 0 || slot.bookings.length > 0) {
    return "Parcial";
  }

  return "Aberto";
}

function isActiveBooking(booking: Booking) {
  return String(booking.status || "CREATED").toUpperCase() !== "CANCELLED";
}

export function AgendaPage({ onApiStatusChange }: AgendaPageProps) {
  const { role } = useAuth();
  const [selectedDate, setSelectedDate] = useState(todayIsoDate());
  const [teeTimes, setTeeTimes] = useState<TeeTime[]>([]);
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [bookingPlayers, setBookingPlayers] = useState<BookingPlayer[]>([]);
  const [rentalTransactions, setRentalTransactions] = useState<RentalTransaction[]>([]);
  const [payments, setPayments] = useState<Payment[]>([]);
  const [receipts, setReceipts] = useState<Receipt[]>([]);
  const [receiptItems, setReceiptItems] = useState<ReceiptItem[]>([]);
  const [checkInTickets, setCheckInTickets] = useState<CheckInTicket[]>([]);
  const [players, setPlayers] = useState<Player[]>([]);
  const [rentalItems, setRentalItems] = useState<RentalItem[]>([]);
  const [selectedBookingId, setSelectedBookingId] = useState<number | null>(null);
  const [isBookingSheetOpen, setIsBookingSheetOpen] = useState(false);
  const [confirmation, setConfirmation] = useState<ConfirmationState | null>(null);
  const [selectedReceiptId, setSelectedReceiptId] = useState<number | null>(null);
  const [selectedCheckInTicketId, setSelectedCheckInTicketId] = useState<number | null>(null);
  const [activeDetailTab, setActiveDetailTab] = useState<BookingDetailTab>("summary");
  const [selectedPlayerId, setSelectedPlayerId] = useState("");
  const [newBookingPlayerGreenFee, setNewBookingPlayerGreenFee] = useState("");
  const [newBookingPlayerCount, setNewBookingPlayerCount] = useState("1");
  const [newBookingPlayerCheckedIn, setNewBookingPlayerCheckedIn] = useState(false);
  const [bookingPlayerFeedback, setBookingPlayerFeedback] = useState<Feedback>({
    message: "",
    type: ""
  });
  const [editingRentalTransactionId, setEditingRentalTransactionId] = useState<number | null>(null);
  const [selectedRentalBookingPlayerId, setSelectedRentalBookingPlayerId] = useState("");
  const [selectedRentalItemId, setSelectedRentalItemId] = useState("");
  const [rentalQuantity, setRentalQuantity] = useState("1");
  const [rentalStatus, setRentalStatus] = useState("RENTED");
  const [returnInspectionRentalId, setReturnInspectionRentalId] = useState<number | null>(null);
  const [returnDamageUnitLabel, setReturnDamageUnitLabel] = useState("");
  const [returnDamageDescription, setReturnDamageDescription] = useState("");
  const [editingPaymentId, setEditingPaymentId] = useState<number | null>(null);
  const [selectedPaymentBookingPlayerId, setSelectedPaymentBookingPlayerId] = useState("");
  const [paymentAmount, setPaymentAmount] = useState("");
  const [paymentMethod, setPaymentMethod] = useState("CARD");
  const [paymentStatus, setPaymentStatus] = useState("PAID");
  const [receiptFeedback, setReceiptFeedback] = useState<Feedback>({
    message: "",
    type: ""
  });
  const [feedback, setFeedback] = useState<Feedback>({
    message: "Escolha um dia e atualize a agenda para consultar a API.",
    type: "success"
  });
  const [apiStatus, setApiStatus] = useState("Aguardando consulta");
  const [requestJson, setRequestJson] = useState("Nenhuma requisicao enviada ainda.");
  const [responseJson, setResponseJson] = useState("Nenhuma resposta recebida ainda.");
  const [isLoading, setIsLoading] = useState(false);
  const canDelete = canDeleteRecords(role);
  const canManageFinancial = canManageFinancialAdjustments(role);
  const canCancelReceipt = canCancelReceipts(role);

  useEffect(() => {
    onApiStatusChange(apiStatus);
  }, [apiStatus, onApiStatusChange]);

  function requestConfirmation(nextConfirmation: ConfirmationState) {
    setConfirmation(nextConfirmation);
  }

  async function handleConfirmAction() {
    if (!confirmation) {
      return;
    }

    const action = confirmation.action;
    setConfirmation(null);
    await action();
  }

  const slots = useMemo(() => generateTimeSlots(), []);
  const dailyTeeTimes = useMemo(
    () => teeTimes.filter((teeTime) => teeTime.playDate === selectedDate),
    [selectedDate, teeTimes]
  );
  const dailyBookings = useMemo(() => {
    const dailyTeeTimeIds = new Set(dailyTeeTimes.map((teeTime) => teeTime.id));
    return bookings.filter((booking) => dailyTeeTimeIds.has(booking.teeTimeId) && isActiveBooking(booking));
  }, [bookings, dailyTeeTimes]);
  const agendaSlots = useMemo<AgendaSlot[]>(
    () =>
      slots.map((time) => {
        const teeTime = dailyTeeTimes.find((item) => formatTime(item.startTime) === time);
        const slotBookings = teeTime?.id
          ? bookings.filter((booking) => booking.teeTimeId === teeTime.id && isActiveBooking(booking))
          : [];

        return {
          time,
          teeTime,
          bookings: slotBookings
        };
      }),
    [bookings, dailyTeeTimes, slots]
  );
  const selectedBooking = useMemo(
    () => bookings.find((booking) => booking.id === selectedBookingId),
    [bookings, selectedBookingId]
  );
  const selectedTeeTime = useMemo(
    () => teeTimes.find((teeTime) => selectedBooking && teeTime.id === selectedBooking.teeTimeId),
    [selectedBooking, teeTimes]
  );
  const selectedBookingPlayers = useMemo(
    () => bookingPlayers.filter((bookingPlayer) => bookingPlayer.bookingId === selectedBookingId),
    [bookingPlayers, selectedBookingId]
  );
  const selectedBookingPlayerIds = useMemo(
    () => new Set(selectedBookingPlayers.map((bookingPlayer) => bookingPlayer.id)),
    [selectedBookingPlayers]
  );
  const selectedRentalTransactions = useMemo(
    () => rentalTransactions.filter((rentalTransaction) =>
      rentalTransaction.bookingId === selectedBookingId
      && selectedBookingPlayerIds.has(rentalTransaction.bookingPlayerId)
    ),
    [rentalTransactions, selectedBookingId, selectedBookingPlayerIds]
  );
  const selectedPayments = useMemo(
    () => payments.filter((payment) =>
      payment.bookingId === selectedBookingId
      && selectedBookingPlayerIds.has(payment.bookingPlayerId)
    ),
    [payments, selectedBookingId, selectedBookingPlayerIds]
  );
  const selectedReceipts = useMemo(
    () => receipts.filter((receipt) =>
      receipt.bookingId === selectedBookingId
      && selectedBookingPlayerIds.has(receipt.bookingPlayerId)
    ),
    [receipts, selectedBookingId, selectedBookingPlayerIds]
  );
  const selectedReceipt = useMemo(
    () => receipts.find((receipt) => receipt.id === selectedReceiptId),
    [receipts, selectedReceiptId]
  );
  const selectedReceiptItems = useMemo(
    () => receiptItems.filter((receiptItem) => receiptItem.receiptId === selectedReceiptId),
    [receiptItems, selectedReceiptId]
  );
  const selectedCheckInTicket = useMemo(
    () => checkInTickets.find((ticket) => ticket.id === selectedCheckInTicketId),
    [checkInTickets, selectedCheckInTicketId]
  );
  const sortedPlayers = useMemo(
    () => [...players].sort((first, second) => first.fullName.localeCompare(second.fullName)),
    [players]
  );
  const activeRentalItems = useMemo(
    () => rentalItems.filter((rentalItem) => rentalItem.active !== false),
    [rentalItems]
  );
  const editingRentalTransaction = useMemo(
    () => rentalTransactions.find((rentalTransaction) => rentalTransaction.id === editingRentalTransactionId),
    [editingRentalTransactionId, rentalTransactions]
  );
  const editingPayment = useMemo(
    () => payments.find((payment) => payment.id === editingPaymentId),
    [editingPaymentId, payments]
  );
  const selectedPlayerCount = useMemo(
    () => selectedBookingPlayers.reduce((total, bookingPlayer) => total + getBookingPlayerCount(bookingPlayer), 0),
    [selectedBookingPlayers]
  );
  const selectedGreenFeeTotal = useMemo(
    () =>
      selectedBookingPlayers.reduce(
        (total, bookingPlayer) => total + getBookingPlayerGreenFeeTotal(bookingPlayer),
        0
      ),
    [selectedBookingPlayers]
  );
  const selectedRentalTotal = useMemo(
    () =>
      selectedRentalTransactions
        .filter((rentalTransaction) => String(rentalTransaction.status || "RENTED").toUpperCase() !== "CANCELLED")
        .reduce((total, rentalTransaction) => total + Number(rentalTransaction.totalPrice || 0), 0),
    [selectedRentalTransactions]
  );
  const selectedPaidTotal = useMemo(
    () =>
      selectedPayments
        .filter((payment) => String(payment.status || "PAID").toUpperCase() === "PAID")
        .reduce((total, payment) => total + Number(payment.amount || 0), 0),
    [selectedPayments]
  );
  const selectedPendingTotal = useMemo(
    () =>
      selectedBookingPlayers.reduce(
        (total, bookingPlayer) => total + getBookingPlayerPendingAmount(bookingPlayer.id),
        0
      ),
    [payments, rentalTransactions, selectedBookingPlayers]
  );
  const selectedCheckedInCount = useMemo(
    () =>
      selectedBookingPlayers
        .filter((bookingPlayer) => bookingPlayer.checkedIn)
        .reduce((total, bookingPlayer) => total + getBookingPlayerCount(bookingPlayer), 0),
    [selectedBookingPlayers]
  );
  const selectedActiveRentalCount = useMemo(
    () =>
      selectedRentalTransactions.filter((rentalTransaction) =>
        isRentalStockReserved(rentalTransaction.status)
      ).length,
    [selectedRentalTransactions]
  );
  const selectedReturnedRentalCount = useMemo(
    () =>
      selectedRentalTransactions.filter((rentalTransaction) =>
        String(rentalTransaction.status || "RENTED").toUpperCase() === "RETURNED"
      ).length,
    [selectedRentalTransactions]
  );
  const selectedPaidPaymentCount = useMemo(
    () =>
      selectedPayments.filter((payment) => String(payment.status || "PAID").toUpperCase() === "PAID").length,
    [selectedPayments]
  );
  const selectedRefundedPaymentCount = useMemo(
    () =>
      selectedPayments.filter((payment) => String(payment.status || "PAID").toUpperCase() === "REFUNDED").length,
    [selectedPayments]
  );
  const isSelectedBookingReady = useMemo(
    () =>
      selectedPlayerCount > 0
      && selectedCheckedInCount === selectedPlayerCount
      && selectedPendingTotal <= 0,
    [selectedPlayerCount, selectedCheckedInCount, selectedPendingTotal]
  );

  function showRequest(method: string, url: string, body?: unknown) {
    setRequestJson(formatJson({ method, url, body: body ?? null }));
  }

  function showResponse(data: unknown) {
    setResponseJson(formatJson(data));
  }

  function applyAgendaDayData(data: AgendaDay) {
    setPlayers(data.players);
    setRentalItems(data.rentalItems);
    setTeeTimes(data.teeTimes);
    setBookings(data.bookings);
    setBookingPlayers(data.bookingPlayers);
    setRentalTransactions(data.rentalTransactions);
    setPayments(data.payments);
    setReceipts(data.receipts);
    setReceiptItems(data.receiptItems);
    setCheckInTickets(data.checkInTickets);
  }

  function getPlayerName(playerId: number) {
    return players.find((player) => player.id === playerId)?.fullName || `Player #${playerId}`;
  }

  function getBookingPlayerRentalTotal(bookingPlayerId: number | undefined) {
    if (!bookingPlayerId) {
      return 0;
    }

    return rentalTransactions
      .filter((rentalTransaction) => rentalTransaction.bookingPlayerId === bookingPlayerId)
      .filter((rentalTransaction) => String(rentalTransaction.status || "RENTED").toUpperCase() !== "CANCELLED")
      .reduce((total, rentalTransaction) => total + Number(rentalTransaction.totalPrice || 0), 0);
  }

  function getBookingPlayerCount(bookingPlayer: BookingPlayer) {
    return Number(bookingPlayer.playerCount || 1);
  }

  function getBookingPlayerGreenFeeTotal(bookingPlayer: BookingPlayer) {
    return Number(bookingPlayer.greenFeeAmount || 0) * getBookingPlayerCount(bookingPlayer);
  }

  function getBookingPlayerTotal(bookingPlayer: BookingPlayer) {
    return getBookingPlayerGreenFeeTotal(bookingPlayer) + getBookingPlayerRentalTotal(bookingPlayer.id);
  }

  function getBookingPlayerPaidAmount(bookingPlayerId: number | undefined, ignoredPaymentId: number | null = null) {
    if (!bookingPlayerId) {
      return 0;
    }

    return payments
      .filter((payment) => payment.bookingPlayerId === bookingPlayerId)
      .filter((payment) => ignoredPaymentId === null || payment.id !== ignoredPaymentId)
      .filter((payment) => String(payment.status || "PAID").toUpperCase() === "PAID")
      .reduce((total, payment) => total + Number(payment.amount || 0), 0);
  }

  function getBookingPlayerPendingAmount(bookingPlayerId: number | undefined, ignoredPaymentId: number | null = null) {
    const bookingPlayer = bookingPlayers.find((item) => item.id === bookingPlayerId);

    if (!bookingPlayer) {
      return 0;
    }

    return Math.max(getBookingPlayerTotal(bookingPlayer) - getBookingPlayerPaidAmount(bookingPlayerId, ignoredPaymentId), 0);
  }

  function getBookingPlayerDisplayName(bookingPlayerId: number | undefined) {
    const bookingPlayer = bookingPlayers.find((item) => item.id === bookingPlayerId);
    return bookingPlayer ? getPlayerName(bookingPlayer.playerId) : `Booking player #${bookingPlayerId}`;
  }

  function getRentalIconType(rentalItem: RentalItem): TeeSheetRentalIcon["type"] {
    const name = `${rentalItem.name || ""} ${rentalItem.type || ""}`.toUpperCase();

    if (name.includes("BUGGY")) {
      return "buggy";
    }

    if (name.includes("TROLLEY")) {
      return "trolley";
    }

    if (name.includes("CLUB") || name.includes("TACO") || name.includes("SET")) {
      return "clubs";
    }

    return "other";
  }

  function getBookingPlayerRentalIcons(bookingPlayerId: number | undefined): TeeSheetRentalIcon[] {
    if (!bookingPlayerId) {
      return [];
    }

    const iconsByType = new Map<TeeSheetRentalIcon["type"], TeeSheetRentalIcon>();

    rentalTransactions
      .filter((rentalTransaction) => rentalTransaction.bookingPlayerId === bookingPlayerId)
      .filter((rentalTransaction) => !["CANCELLED", "RETURNED"].includes(String(rentalTransaction.status || "").toUpperCase()))
      .forEach((rentalTransaction) => {
        const rentalItem = rentalItems.find((item) => item.id === rentalTransaction.rentalItemId);

        if (!rentalItem) {
          return;
        }

        const type = getRentalIconType(rentalItem);
        iconsByType.set(type, {
          key: `${type}-${rentalItem.id}`,
          label: rentalItem.name,
          type
        });
      });

    return Array.from(iconsByType.values());
  }

  function getBookingPlayersForBooking(bookingId: number | undefined) {
    if (!bookingId) {
      return [];
    }

    return bookingPlayers.filter((bookingPlayer) => bookingPlayer.bookingId === bookingId);
  }

  function getTeeSheetBookingCells(slot: AgendaSlot): TeeSheetBookingCell[] {
    return slot.bookings.flatMap((booking) =>
      getBookingPlayersForBooking(booking.id).map((bookingPlayer) => {
        const count = Math.min(getBookingPlayerCount(bookingPlayer), slot.teeTime?.maxPlayers || 4);
        const pendingAmount = getBookingPlayerPendingAmount(bookingPlayer.id);
        const isPaid = pendingAmount <= 0 && getBookingPlayerTotal(bookingPlayer) > 0;
        const state = isPaid ? "paid" : bookingPlayer.checkedIn ? "checked-in" : "partial";

        return {
          booking,
          bookingPlayer,
          count,
          label: getPlayerName(bookingPlayer.playerId),
          meta: `${count} player${count === 1 ? "" : "s"} - ${booking.code || `Booking #${booking.id}`}`,
          rentalIcons: getBookingPlayerRentalIcons(bookingPlayer.id),
          state
        };
      })
    );
  }

  function getTeeSheetRowState(slot: AgendaSlot) {
    if (!slot.teeTime) {
      return "free";
    }

    const teeTimeStatus = String(slot.teeTime.status || "").toUpperCase();
    if (teeTimeStatus === "CANCELLED" || teeTimeStatus === "BLOCKED") {
      return "blocked";
    }

    const cells = getTeeSheetBookingCells(slot);
    const maxPlayers = Number(slot.teeTime.maxPlayers || 4);
    const bookedPlayers = Number(slot.teeTime.bookedPlayers || 0);

    if (bookedPlayers <= 0 || cells.length === 0) {
      return "free";
    }

    if (bookedPlayers >= maxPlayers && cells.every((cell) => cell.state === "paid")) {
      return "paid";
    }

    if (cells.every((cell) => cell.state === "checked-in" || cell.state === "paid")) {
      return "checked-in";
    }

    if (bookedPlayers >= maxPlayers) {
      return "full";
    }

    return "partial";
  }

  function getTeeSheetRowLabel(slot: AgendaSlot) {
    const state = getTeeSheetRowState(slot);

    if (state === "blocked") {
      return "Bloqueado";
    }

    if (state === "paid") {
      return "Pago/check-in";
    }

    if (state === "checked-in") {
      return "Check-in";
    }

    if (state === "full") {
      return "Cheio";
    }

    if (state === "partial") {
      return "Parcial";
    }

    return "Livre";
  }

  function getEmptyTeeSheetCellCount(slot: AgendaSlot, cells: TeeSheetBookingCell[]) {
    const maxPlayers = Number(slot.teeTime?.maxPlayers || 4);
    const occupiedCells = cells.reduce((total, cell) => total + cell.count, 0);
    return Math.max(maxPlayers - occupiedCells, 0);
  }

  function getStatusBadgeClass(status: string | null | undefined) {
    const normalizedStatus = String(status || "").toUpperCase();

    if (["PAID", "CONFIRMED", "CHECK-IN", "CHECKED_IN", "RETURNED", "EMITIDO"].includes(normalizedStatus)) {
      return "bg-emerald-100 text-emerald-800 hover:bg-emerald-100";
    }

    if (["PENDING", "CREATED", "PARTIAL", "RENTED", "PENDENTE"].includes(normalizedStatus)) {
      return "bg-blue-100 text-blue-800 hover:bg-blue-100";
    }

    if (["FULL", "DAMAGED", "LOST"].includes(normalizedStatus)) {
      return "bg-amber-100 text-amber-800 hover:bg-amber-100";
    }

    if (["CANCELLED", "CANCELADO", "REFUNDED"].includes(normalizedStatus)) {
      return "bg-red-100 text-red-800 hover:bg-red-100";
    }

    return "bg-slate-100 text-slate-700 hover:bg-slate-100";
  }

  function renderStatusBadge(status: string | null | undefined) {
    return (
      <Badge className={getStatusBadgeClass(status)}>
        {status || "STATUS"}
      </Badge>
    );
  }

  function getRentalBadgeText(icon: TeeSheetRentalIcon) {
    if (icon.type === "buggy") {
      return "BUGGY";
    }

    if (icon.type === "trolley") {
      return "TROLLEY";
    }

    if (icon.type === "clubs") {
      return "CLUBS";
    }

    return "ITEM";
  }

  function getReceiptForPayment(paymentId: number | undefined) {
    if (!paymentId) {
      return undefined;
    }

    return receipts.find((receipt) => receipt.paymentId === paymentId && !receipt.cancelled)
      ?? receipts.find((receipt) => receipt.paymentId === paymentId);
  }

  function getReceiptForBookingPlayer(bookingPlayerId: number | undefined) {
    if (!bookingPlayerId) {
      return undefined;
    }

    return receipts.find((receipt) => receipt.bookingPlayerId === bookingPlayerId && !receipt.cancelled)
      ?? receipts.find((receipt) => receipt.bookingPlayerId === bookingPlayerId);
  }

  function getReceiptStatus(receipt: Receipt | undefined) {
    if (!receipt) {
      return "NAO EMITIDO";
    }

    return receipt.cancelled ? "CANCELADO" : "EMITIDO";
  }

  function getActiveCheckInTicketForBookingPlayer(bookingPlayerId: number | undefined) {
    if (!bookingPlayerId) {
      return undefined;
    }

    return checkInTickets.find((ticket) => ticket.bookingPlayerId === bookingPlayerId && !ticket.cancelled)
      ?? checkInTickets.find((ticket) => ticket.bookingPlayerId === bookingPlayerId);
  }

  function getCheckInTicketStatus(ticket: CheckInTicket | undefined) {
    if (!ticket) {
      return "NAO EMITIDO";
    }

    return ticket.cancelled ? "CANCELADO" : "EMITIDO";
  }

  function getCheckInTicketPlayerCount(ticket: CheckInTicket) {
    return Number(ticket.playerCountSnapshot || 1);
  }

  function getRentalItem(rentalItemId: number | undefined) {
    return rentalItems.find((item) => item.id === rentalItemId);
  }

  function getRentalItemName(rentalItemId: number | undefined) {
    return getRentalItem(rentalItemId)?.name || `Item #${rentalItemId}`;
  }

  function isRentalStockReserved(status: string | null | undefined) {
    const normalizedStatus = String(status || "RENTED").toUpperCase();
    return normalizedStatus === "RENTED" || normalizedStatus === "LOST" || normalizedStatus === "DAMAGED";
  }

  function getAvailableStockForRentalForm(rentalItemId: number | undefined) {
    const rentalItem = getRentalItem(rentalItemId);

    if (!rentalItem) {
      return 0;
    }

    const availableStock = Number(rentalItem.availableStock || 0);
    const currentEditingRentalTransaction = editingRentalTransaction;

    if (
      currentEditingRentalTransaction
      && currentEditingRentalTransaction.rentalItemId === rentalItemId
      && isRentalStockReserved(currentEditingRentalTransaction.status)
    ) {
      return availableStock + Number(currentEditingRentalTransaction.quantity || 0);
    }

    return availableStock;
  }

  function resetRentalTransactionForm() {
    setEditingRentalTransactionId(null);
    setSelectedRentalBookingPlayerId("");
    setSelectedRentalItemId("");
    setRentalQuantity("1");
    setRentalStatus("RENTED");
  }

  function getPaymentFormMaxAmount() {
    const bookingPlayerId = Number(selectedPaymentBookingPlayerId);
    const bookingPlayer = bookingPlayers.find((item) => item.id === bookingPlayerId);

    if (!bookingPlayer) {
      return 0;
    }

    if (paymentStatus === "PAID") {
      return getBookingPlayerPendingAmount(bookingPlayerId, editingPaymentId);
    }

    return getBookingPlayerTotal(bookingPlayer);
  }

  function resetPaymentForm() {
    setEditingPaymentId(null);
    setSelectedPaymentBookingPlayerId("");
    setPaymentAmount("");
    setPaymentMethod("CARD");
    setPaymentStatus("PAID");
  }

  function clearReceiptSelection() {
    setSelectedReceiptId(null);
    setReceiptFeedback({ message: "", type: "" });
  }

  async function loadAgenda() {
    setIsLoading(true);
    setFeedback({ message: "Carregando agenda do dia...", type: "success" });
    showRequest("GET", `/agenda/day?date=${selectedDate}`);

    try {
      const data = await agendaService.findDay(selectedDate);

      applyAgendaDayData(data);
      showResponse(data);
      setApiStatus("Conectada");
      setFeedback({ message: "Agenda carregada com sucesso.", type: "success" });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setApiStatus("Falha na conexao");
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  async function refreshAgendaData() {
    const data = await agendaService.findDay(selectedDate);
    applyAgendaDayData(data);
    return data;
  }

  async function createTeeTimeForSlot(time: string) {
    return teeTimeService.create({
      playDate: selectedDate,
      startTime: time,
      maxPlayers: 4,
      bookedPlayers: 0,
      status: "AVAILABLE",
      baseGreenFee: 0
    });
  }

  async function createBookingForTeeTime(teeTime: TeeTime) {
    if (!teeTime.id) {
      throw new Error("Tee time criado sem id.");
    }

    return bookingService.create({
      code: null,
      status: "CREATED",
      totalAmount: 0,
      teeTimeId: teeTime.id
    });
  }

  async function handleSlotClick(slot: AgendaSlot) {
    setIsLoading(true);
    setFeedback({ message: `Preparando booking para ${slot.time}...`, type: "success" });

    try {
      const existingBooking = slot.bookings.find(isActiveBooking);

      if (slot.teeTime && existingBooking?.id) {
        setSelectedBookingId(existingBooking.id);
        setActiveDetailTab("players");
        setIsBookingSheetOpen(true);
        setFeedback({ message: `Booking #${existingBooking.id} selecionado.`, type: "success" });
        return;
      }

      const teeTime = slot.teeTime ?? await createTeeTimeForSlot(slot.time);
      const booking = existingBooking ?? await createBookingForTeeTime(teeTime);

      if (!booking.id) {
        throw new Error("Booking criado sem id.");
      }

      const refreshedData = await refreshAgendaData();
      setSelectedBookingId(booking.id);
      setActiveDetailTab("players");
      setIsBookingSheetOpen(true);
      showRequest(slot.teeTime ? "POST" : "POST", slot.teeTime ? "/booking" : "/tee-time + /booking", {
        teeTime: slot.teeTime ? undefined : {
          playDate: selectedDate,
          startTime: slot.time,
          maxPlayers: 4,
          bookedPlayers: 0,
          status: "AVAILABLE",
          baseGreenFee: 0
        },
        booking: {
          teeTimeId: teeTime.id,
          status: "CREATED",
          totalAmount: 0
        }
      });
      showResponse({
        selectedBooking: booking,
        refreshedTeeTimes: refreshedData.teeTimes,
        refreshedBookings: refreshedData.bookings,
        refreshedBookingPlayers: refreshedData.bookingPlayers,
        refreshedRentalTransactions: refreshedData.rentalTransactions,
        refreshedPayments: refreshedData.payments,
        refreshedReceipts: refreshedData.receipts,
        refreshedReceiptItems: refreshedData.receiptItems,
        refreshedCheckInTickets: refreshedData.checkInTickets
      });
      setApiStatus("Conectada");
      setFeedback({ message: `Booking #${booking.id} pronto para ${slot.time}.`, type: "success" });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setApiStatus("Falha na conexao");
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  async function handleAddBookingPlayer(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!selectedBooking?.id) {
      setBookingPlayerFeedback({ message: "Selecione um booking antes de adicionar jogador.", type: "error" });
      return;
    }

    if (!selectedPlayerId) {
      setBookingPlayerFeedback({ message: "Selecione um player para adicionar ao booking.", type: "error" });
      return;
    }

    const playerCount = Number(newBookingPlayerCount || 1);

    if (!Number.isInteger(playerCount) || playerCount < 1 || playerCount > 4) {
      setBookingPlayerFeedback({ message: "Quantidade de jogadores deve ser entre 1 e 4.", type: "error" });
      return;
    }

    const payload = {
      bookingId: selectedBooking.id,
      playerId: Number(selectedPlayerId),
      greenFeeAmount: newBookingPlayerGreenFee ? Number(newBookingPlayerGreenFee) : null,
      playerCount,
      checkedIn: newBookingPlayerCheckedIn
    };

    setIsLoading(true);
    setBookingPlayerFeedback({ message: "Adicionando jogador ao booking...", type: "success" });
    showRequest("POST", "/booking-player", payload);

    try {
      const savedBookingPlayer = await bookingPlayerService.create(payload);
      const refreshedData = await refreshAgendaData();
      setSelectedBookingId(savedBookingPlayer.bookingId);
      setActiveDetailTab("players");
      setSelectedPlayerId("");
      setNewBookingPlayerGreenFee("");
      setNewBookingPlayerCount("1");
      setNewBookingPlayerCheckedIn(false);
      setApiStatus("Conectada");
      setBookingPlayerFeedback({ message: "Jogador adicionado ao booking com sucesso.", type: "success" });
      showResponse({
        savedBookingPlayer,
        refreshedBooking: refreshedData.bookings.find((booking) => booking.id === savedBookingPlayer.bookingId),
        refreshedTeeTime: refreshedData.teeTimes.find(
          (teeTime) =>
            teeTime.id === refreshedData.bookings.find((booking) => booking.id === savedBookingPlayer.bookingId)?.teeTimeId
        ),
        refreshedBookingPlayers: refreshedData.bookingPlayers.filter(
          (bookingPlayer) => bookingPlayer.bookingId === savedBookingPlayer.bookingId
        ),
        refreshedCheckInTickets: refreshedData.checkInTickets.filter(
          (ticket) => ticket.bookingPlayerId === savedBookingPlayer.id
        )
      });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setApiStatus("Falha na conexao");
      setBookingPlayerFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  async function handleToggleBookingPlayerCheckIn(bookingPlayer: BookingPlayer) {
    if (!bookingPlayer.id) {
      return;
    }

    const payload = {
      id: bookingPlayer.id,
      bookingId: bookingPlayer.bookingId,
      playerId: bookingPlayer.playerId,
      greenFeeAmount: bookingPlayer.greenFeeAmount,
      playerCount: getBookingPlayerCount(bookingPlayer),
      checkedIn: !bookingPlayer.checkedIn
    };

    setIsLoading(true);
    showRequest("PUT", "/booking-player", payload);

    try {
      const savedBookingPlayer = await bookingPlayerService.update(payload);
      const refreshedData = await refreshAgendaData();
      const refreshedTicket = refreshedData.checkInTickets.find(
        (ticket) => ticket.bookingPlayerId === savedBookingPlayer.id && !ticket.cancelled
      );
      setSelectedBookingId(savedBookingPlayer.bookingId);
      setActiveDetailTab("players");
      setSelectedCheckInTicketId(savedBookingPlayer.checkedIn ? refreshedTicket?.id ?? null : null);
      setApiStatus("Conectada");
      setFeedback({ message: "Check-in atualizado com sucesso.", type: "success" });
      showResponse({
        savedBookingPlayer,
        refreshedBooking: refreshedData.bookings.find((booking) => booking.id === savedBookingPlayer.bookingId),
        refreshedCheckInTickets: refreshedData.checkInTickets.filter(
          (ticket) => ticket.bookingPlayerId === savedBookingPlayer.id
        )
      });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setApiStatus("Falha na conexao");
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  async function handleRemoveBookingPlayer(bookingPlayer: BookingPlayer) {
    if (!bookingPlayer.id) {
      return;
    }

    setIsLoading(true);
    showRequest("DELETE", `/booking-player/${bookingPlayer.id}`);

    try {
      await bookingPlayerService.remove(bookingPlayer.id);
      const refreshedData = await refreshAgendaData();
      setSelectedBookingId(bookingPlayer.bookingId);
      setActiveDetailTab("players");
      setApiStatus("Conectada");
      setFeedback({ message: "Jogador removido do booking com sucesso.", type: "success" });
      showResponse({
        deletedBookingPlayerId: bookingPlayer.id,
        refreshedBooking: refreshedData.bookings.find((booking) => booking.id === bookingPlayer.bookingId),
        refreshedBookingPlayers: refreshedData.bookingPlayers.filter(
          (item) => item.bookingId === bookingPlayer.bookingId
        )
      });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setApiStatus("Falha na conexao");
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  async function handleRentalTransactionSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!selectedBooking?.id) {
      setFeedback({ message: "Selecione um booking antes de adicionar material.", type: "error" });
      return;
    }

    if (!selectedRentalBookingPlayerId) {
      setFeedback({ message: "Selecione para qual jogador o material sera alugado.", type: "error" });
      return;
    }

    if (!selectedRentalItemId) {
      setFeedback({ message: "Selecione um material.", type: "error" });
      return;
    }

    const quantity = Number(rentalQuantity);
    const rentalItemId = Number(selectedRentalItemId);
    const availableStock = getAvailableStockForRentalForm(rentalItemId);

    if (!Number.isInteger(quantity) || quantity < 1) {
      setFeedback({ message: "Quantidade deve ser pelo menos 1.", type: "error" });
      return;
    }

    if (isRentalStockReserved(rentalStatus) && quantity > availableStock) {
      setFeedback({ message: "Quantidade maior que o estoque disponivel.", type: "error" });
      return;
    }

    const payload = {
      id: editingRentalTransactionId ?? undefined,
      bookingId: selectedBooking.id,
      bookingPlayerId: Number(selectedRentalBookingPlayerId),
      rentalItemId,
      quantity,
      status: rentalStatus,
      unitPrice: editingRentalTransaction?.unitPrice ?? null,
      totalPrice: editingRentalTransaction?.totalPrice ?? null
    };

    setIsLoading(true);
    showRequest(editingRentalTransactionId ? "PUT" : "POST", "/rental-transaction", payload);

    try {
      const savedRentalTransaction = editingRentalTransactionId
        ? await rentalTransactionService.update(payload)
        : await rentalTransactionService.create(payload);
      const refreshedData = await refreshAgendaData();
      setSelectedBookingId(savedRentalTransaction.bookingId);
      setActiveDetailTab("rentals");
      resetRentalTransactionForm();
      setApiStatus("Conectada");
      setFeedback({
        message: editingRentalTransactionId ? "Rental atualizado com sucesso." : "Material adicionado ao booking.",
        type: "success"
      });
      showResponse({
        savedRentalTransaction,
        refreshedBooking: refreshedData.bookings.find((booking) => booking.id === savedRentalTransaction.bookingId),
        refreshedRentalTransactions: refreshedData.rentalTransactions.filter(
          (rentalTransaction) => rentalTransaction.bookingId === savedRentalTransaction.bookingId
        ),
        refreshedRentalItems: refreshedData.rentalItems
      });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setApiStatus("Falha na conexao");
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  function handleEditRentalTransaction(rentalTransaction: RentalTransaction) {
    resetReturnInspection();
    setEditingRentalTransactionId(rentalTransaction.id ?? null);
    setSelectedRentalBookingPlayerId(String(rentalTransaction.bookingPlayerId));
    setSelectedRentalItemId(String(rentalTransaction.rentalItemId));
    setRentalQuantity(String(rentalTransaction.quantity || 1));
    setRentalStatus(String(rentalTransaction.status || "RENTED").toUpperCase());
    setActiveDetailTab("rentals");
  }

  function startReturnInspection(rentalTransaction: RentalTransaction) {
    if (!rentalTransaction.id) {
      return;
    }

    setReturnInspectionRentalId(Number(rentalTransaction.id));
    setReturnDamageUnitLabel("");
    setReturnDamageDescription("");
    setActiveDetailTab("rentals");
  }

  function resetReturnInspection() {
    setReturnInspectionRentalId(null);
    setReturnDamageUnitLabel("");
    setReturnDamageDescription("");
  }

  async function handleReturnRentalTransaction(rentalTransaction: RentalTransaction, status: "RETURNED" | "DAMAGED") {
    if (!rentalTransaction.id) {
      return;
    }

    if (status === "DAMAGED" && !returnDamageDescription.trim()) {
      setFeedback({ message: "Descreva a avaria antes de registrar o material como danificado.", type: "error" });
      return;
    }

    const payload = {
      id: rentalTransaction.id,
      bookingId: rentalTransaction.bookingId,
      bookingPlayerId: rentalTransaction.bookingPlayerId,
      rentalItemId: rentalTransaction.rentalItemId,
      quantity: rentalTransaction.quantity,
      status,
      unitPrice: rentalTransaction.unitPrice,
      totalPrice: rentalTransaction.totalPrice
    };

    setIsLoading(true);
    const damagePayload = {
      damagedUnitLabel: returnDamageUnitLabel.trim() || null,
      description: returnDamageDescription.trim(),
      status: "OPEN"
    };
    showRequest(
      status === "DAMAGED" ? "POST" : "PUT",
      status === "DAMAGED" ? `/rental-damage-report/rental-transaction/${rentalTransaction.id}/damage` : "/rental-transaction",
      status === "DAMAGED" ? damagePayload : payload
    );

    try {
      const damageReport = status === "DAMAGED"
        ? await rentalDamageReportService.reportTransactionDamage(rentalTransaction.id, damagePayload)
        : null;
      const savedRentalTransaction = status === "DAMAGED"
        ? { ...rentalTransaction, status: "DAMAGED" }
        : await rentalTransactionService.update(payload);
      const refreshedData = await refreshAgendaData();
      setSelectedBookingId(savedRentalTransaction.bookingId);
      setActiveDetailTab("rentals");
      resetRentalTransactionForm();
      resetReturnInspection();
      setApiStatus("Conectada");
      setFeedback({
        message: status === "DAMAGED"
          ? "Avaria registrada. Material marcado como danificado e mantido fora do estoque disponivel."
          : "Material devolvido ao estoque com sucesso.",
        type: "success"
      });
      showResponse({
        savedRentalTransaction,
        damageReport,
        refreshedRentalItems: refreshedData.rentalItems,
        refreshedBooking: refreshedData.bookings.find((booking) => booking.id === savedRentalTransaction.bookingId)
      });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setApiStatus("Falha na conexao");
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  async function handleDeleteRentalTransaction(rentalTransaction: RentalTransaction) {
    if (!rentalTransaction.id) {
      return;
    }

    resetReturnInspection();
    setIsLoading(true);
    showRequest("DELETE", `/rental-transaction/${rentalTransaction.id}`);

    try {
      await rentalTransactionService.remove(rentalTransaction.id);
      const refreshedData = await refreshAgendaData();
      setSelectedBookingId(rentalTransaction.bookingId);
      setActiveDetailTab("rentals");
      resetRentalTransactionForm();
      setApiStatus("Conectada");
      setFeedback({ message: "Rental excluido com sucesso.", type: "success" });
      showResponse({
        deletedRentalTransactionId: rentalTransaction.id,
        refreshedBooking: refreshedData.bookings.find((booking) => booking.id === rentalTransaction.bookingId),
        refreshedRentalTransactions: refreshedData.rentalTransactions.filter(
          (item) => item.bookingId === rentalTransaction.bookingId
        ),
        refreshedRentalItems: refreshedData.rentalItems
      });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setApiStatus("Falha na conexao");
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  async function handlePaymentSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!selectedBooking?.id) {
      setFeedback({ message: "Selecione um booking antes de registrar pagamento.", type: "error" });
      return;
    }

    if (!selectedPaymentBookingPlayerId) {
      setFeedback({ message: "Selecione para qual jogador o pagamento sera registrado.", type: "error" });
      return;
    }

    const amount = Number(paymentAmount);
    const maxAmount = getPaymentFormMaxAmount();

    if (!amount || amount <= 0) {
      setFeedback({ message: "Valor do pagamento deve ser maior que zero.", type: "error" });
      return;
    }

    if (amount > maxAmount) {
      setFeedback({ message: "Valor informado excede o saldo permitido para este jogador.", type: "error" });
      return;
    }

    const payload = {
      id: editingPaymentId ?? undefined,
      bookingId: selectedBooking.id,
      bookingPlayerId: Number(selectedPaymentBookingPlayerId),
      amount,
      method: paymentMethod,
      status: paymentStatus,
      paidAt: editingPayment?.paidAt ?? null
    };

    setIsLoading(true);
    showRequest(editingPaymentId ? "PUT" : "POST", "/payment", payload);

    try {
      const savedPayment = editingPaymentId
        ? await paymentService.update(payload)
        : await paymentService.create(payload);
      const refreshedData = await refreshAgendaData();
      setSelectedBookingId(savedPayment.bookingId);
      setActiveDetailTab("payments");
      resetPaymentForm();
      clearReceiptSelection();
      setApiStatus("Conectada");
      setFeedback({
        message: editingPaymentId ? "Pagamento atualizado com sucesso." : "Pagamento registrado com sucesso.",
        type: "success"
      });
      showResponse({
        savedPayment,
        refreshedBooking: refreshedData.bookings.find((booking) => booking.id === savedPayment.bookingId),
        refreshedPayments: refreshedData.payments.filter((payment) => payment.bookingId === savedPayment.bookingId),
        refreshedReceipts: refreshedData.receipts.filter((receipt) => receipt.bookingId === savedPayment.bookingId),
        refreshedReceiptItems: refreshedData.receiptItems
      });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setApiStatus("Falha na conexao");
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  function handleEditPayment(payment: Payment) {
    setEditingPaymentId(payment.id ?? null);
    setSelectedPaymentBookingPlayerId(String(payment.bookingPlayerId));
    setPaymentAmount(String(payment.amount || ""));
    setPaymentMethod(String(payment.method || "CARD").toUpperCase());
    setPaymentStatus(String(payment.status || "PAID").toUpperCase());
    setActiveDetailTab("payments");
  }

  async function handleRefundPayment(payment: Payment) {
    if (!payment.id) {
      return;
    }

    const payload = {
      id: payment.id,
      bookingId: payment.bookingId,
      bookingPlayerId: payment.bookingPlayerId,
      amount: payment.amount,
      method: payment.method,
      status: "REFUNDED",
      paidAt: payment.paidAt ?? null
    };

    setIsLoading(true);
    showRequest("PUT", "/payment", payload);

    try {
      const savedPayment = await paymentService.update(payload);
      const refreshedData = await refreshAgendaData();
      const refreshedBooking = refreshedData.bookings.find((booking) => booking.id === savedPayment.bookingId);
      const refreshedBookingPlayers = refreshedData.bookingPlayers.filter(
        (bookingPlayer) => bookingPlayer.bookingId === savedPayment.bookingId
      );
      setSelectedBookingId(refreshedBooking && isActiveBooking(refreshedBooking) && refreshedBookingPlayers.length > 0
        ? savedPayment.bookingId
        : null);
      clearReceiptSelection();
      setActiveDetailTab("payments");
      resetPaymentForm();
      setApiStatus("Conectada");
      setFeedback({ message: "Pagamento reembolsado com sucesso.", type: "success" });
      showResponse({
        savedPayment,
        refreshedBooking,
        refreshedBookingPlayers,
        refreshedPayments: refreshedData.payments.filter((item) => item.bookingId === savedPayment.bookingId),
        refreshedReceipts: refreshedData.receipts.filter((receipt) => receipt.bookingId === savedPayment.bookingId),
        refreshedReceiptItems: refreshedData.receiptItems
      });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setApiStatus("Falha na conexao");
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  async function handleDeletePayment(payment: Payment) {
    if (!payment.id) {
      return;
    }

    setIsLoading(true);
    showRequest("DELETE", `/payment/${payment.id}`);

    try {
      await paymentService.remove(payment.id);
      const refreshedData = await refreshAgendaData();
      setSelectedBookingId(payment.bookingId);
      clearReceiptSelection();
      setActiveDetailTab("payments");
      resetPaymentForm();
      setApiStatus("Conectada");
      setFeedback({ message: "Pagamento excluido com sucesso.", type: "success" });
      showResponse({
        deletedPaymentId: payment.id,
        refreshedBooking: refreshedData.bookings.find((booking) => booking.id === payment.bookingId),
        refreshedPayments: refreshedData.payments.filter((item) => item.bookingId === payment.bookingId),
        refreshedReceipts: refreshedData.receipts.filter((receipt) => receipt.bookingId === payment.bookingId),
        refreshedReceiptItems: refreshedData.receiptItems
      });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setApiStatus("Falha na conexao");
      setFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  async function handleOpenReceipt(payment: Payment) {
    const existingReceipt = getReceiptForPayment(payment.id);

    if (existingReceipt?.id) {
      setSelectedReceiptId(existingReceipt.id);
      setReceiptFeedback({
        message: existingReceipt.cancelled ? "Recibo cancelado aberto para consulta." : "Recibo aberto para impressao.",
        type: existingReceipt.cancelled ? "error" : "success"
      });
      setActiveDetailTab("payments");
      return;
    }

    if (!payment.id) {
      setReceiptFeedback({ message: "Pagamento sem id nao pode gerar recibo.", type: "error" });
      return;
    }

    if (String(payment.status || "PAID").toUpperCase() !== "PAID") {
      setReceiptFeedback({ message: "Somente pagamentos PAID podem emitir recibo.", type: "error" });
      return;
    }

    setIsLoading(true);
    showRequest("POST", `/receipt/payment/${payment.id}/issue`);

    try {
      const issuedReceipt = await receiptService.issueByPaymentId(payment.id);
      const refreshedData = await refreshAgendaData();
      const refreshedReceipt = refreshedData.receipts.find((receipt) => receipt.id === issuedReceipt.id)
        ?? issuedReceipt;
      setSelectedBookingId(refreshedReceipt.bookingId);
      setSelectedReceiptId(refreshedReceipt.id ?? null);
      setActiveDetailTab("payments");
      setApiStatus("Conectada");
      setReceiptFeedback({ message: "Recibo emitido e pronto para impressao.", type: "success" });
      showResponse({
        issuedReceipt: refreshedReceipt,
        receiptItems: refreshedData.receiptItems.filter((item) => item.receiptId === refreshedReceipt.id)
      });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setApiStatus("Falha na conexao");
      setReceiptFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  async function handleCancelReceipt(receipt: Receipt) {
    if (!receipt.id) {
      return;
    }

    setIsLoading(true);
    showRequest("PUT", `/receipt/${receipt.id}/cancel`, { reason: "Cancelled by operator" });

    try {
      const cancelledReceipt = await receiptService.cancel(receipt.id, "Cancelled by operator");
      const refreshedData = await refreshAgendaData();
      const refreshedReceipt = refreshedData.receipts.find((item) => item.id === cancelledReceipt.id)
        ?? cancelledReceipt;
      setSelectedBookingId(refreshedReceipt.bookingId);
      setSelectedReceiptId(refreshedReceipt.id ?? null);
      setActiveDetailTab("payments");
      setApiStatus("Conectada");
      setReceiptFeedback({ message: "Recibo cancelado com sucesso.", type: "success" });
      showResponse({
        cancelledReceipt: refreshedReceipt,
        refreshedReceipts: refreshedData.receipts.filter((item) => item.bookingId === refreshedReceipt.bookingId)
      });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setApiStatus("Falha na conexao");
      setReceiptFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  async function handleShowCheckInTicket(bookingPlayer: BookingPlayer) {
    if (!bookingPlayer.id) {
      return;
    }

    setIsLoading(true);
    showRequest("POST", `/check-in-ticket/booking-player/${bookingPlayer.id}/issue`);

    try {
      const existingTicket = getActiveCheckInTicketForBookingPlayer(bookingPlayer.id);
      const ticket = existingTicket && !existingTicket.cancelled
        ? existingTicket
        : await checkInTicketService.issueByBookingPlayerId(bookingPlayer.id);
      const refreshedData = await refreshAgendaData();
      const refreshedTicket = refreshedData.checkInTickets.find((item) => item.id === ticket.id) ?? ticket;
      setSelectedBookingId(bookingPlayer.bookingId);
      setSelectedCheckInTicketId(refreshedTicket.id ?? null);
      setActiveDetailTab("players");
      setApiStatus("Conectada");
      setBookingPlayerFeedback({ message: "Ticket de check-in pronto para impressao.", type: "success" });
      showResponse({
        checkInTicket: refreshedTicket,
        refreshedCheckInTickets: refreshedData.checkInTickets.filter((item) => item.bookingPlayerId === bookingPlayer.id)
      });
    } catch (error) {
      const message = getApiErrorMessage(error);
      setApiStatus("Falha na conexao");
      setBookingPlayerFeedback({ message, type: "error" });
      showResponse(getApiErrorResponse(error));
    } finally {
      setIsLoading(false);
    }
  }

  function handlePrintPanel(selector: string, title: string) {
    const receiptElement = document.querySelector<HTMLElement>(selector);

    if (!receiptElement) {
      window.print();
      return;
    }

    const printWindow = window.open("", "receipt-print", "width=900,height=700");

    if (!printWindow) {
      window.print();
      return;
    }

    printWindow.document.open();
    printWindow.document.write(`
      <!doctype html>
      <html>
        <head>
          <title>${title}</title>
          <style>
            @page {
              margin: 14mm;
            }

            * {
              box-sizing: border-box;
            }

            body {
              margin: 0;
              background: #fff;
              color: #18251d;
              font-family: Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
            }

            .receipt-paper {
              display: grid;
              gap: 16px;
              width: 100%;
              max-width: 760px;
              margin: 0 auto;
              background: #fff;
              color: #18251d;
              padding: 0;
            }

            .receipt-paper h3 {
              margin: 0;
            }

            .receipt-header {
              display: flex;
              align-items: start;
              justify-content: space-between;
              gap: 18px;
              border-bottom: 1px solid rgba(24, 37, 29, 0.14);
              padding-bottom: 14px;
            }

            .receipt-kicker {
              margin: 0 0 6px;
              color: #54735e;
              font-size: 0.82rem;
              font-weight: 800;
              letter-spacing: 0.08em;
              text-transform: uppercase;
            }

            .receipt-status {
              border-radius: 999px;
              background: rgba(93, 145, 77, 0.14);
              color: #285b2b;
              font-size: 0.78rem;
              font-weight: 900;
              padding: 7px 10px;
            }

            .receipt-status.cancelled {
              background: rgba(183, 58, 58, 0.14);
              color: #8f2020;
            }

            .receipt-meta {
              display: grid;
              grid-template-columns: 120px 1fr;
              gap: 8px 14px;
            }

            .receipt-meta span {
              color: #607267;
              font-size: 0.88rem;
            }

            .ticket-route-grid {
              display: grid;
              grid-template-columns: repeat(2, minmax(0, 1fr));
              gap: 14px;
            }

            .ticket-route-grid > div {
              display: grid;
              gap: 8px;
              border: 1px solid rgba(24, 37, 29, 0.12);
              border-radius: 8px;
              padding: 16px;
            }

            .ticket-route-grid span {
              color: #607267;
              font-size: 0.78rem;
              font-weight: 900;
              letter-spacing: 0.08em;
              text-transform: uppercase;
            }

            .ticket-route-grid strong:last-child {
              font-size: 1.45rem;
            }

            .receipt-items {
              display: grid;
              gap: 8px;
            }

            .receipt-item-row {
              display: grid;
              grid-template-columns: minmax(180px, 1fr) 70px 110px 110px;
              gap: 12px;
              align-items: center;
              border-bottom: 1px solid rgba(24, 37, 29, 0.1);
              padding: 9px 0;
            }

            .receipt-item-row.header {
              color: #607267;
              font-size: 0.78rem;
              font-weight: 900;
              letter-spacing: 0.08em;
              text-transform: uppercase;
            }

            .receipt-total-line {
              display: flex;
              justify-content: space-between;
              gap: 16px;
              border-top: 1px solid rgba(24, 37, 29, 0.12);
              padding-top: 10px;
            }

            .receipt-total-line.grand-total {
              font-size: 1.2rem;
            }

            .receipt-cancel-note {
              margin: 0;
              border-radius: 8px;
              background: rgba(183, 58, 58, 0.1);
              color: #8f2020;
              padding: 12px;
            }
          </style>
        </head>
        <body>
          ${receiptElement.outerHTML}
          <script>
            window.addEventListener("load", function () {
              window.focus();
              window.print();
              window.close();
            });
          </script>
        </body>
      </html>
    `);
    printWindow.document.close();
  }

  function handlePrintReceipt() {
    handlePrintPanel("#receipt-print-area .receipt-paper", selectedReceipt?.receiptNumber || "Recibo");
  }

  function handlePrintCheckInTicket() {
    handlePrintPanel("#check-in-ticket-print-area .receipt-paper", selectedCheckInTicket?.ticketNumber || "Ticket check-in");
  }

  useEffect(() => {
    void loadAgenda();
  }, [selectedDate]);

  useEffect(() => {
    clearReceiptSelection();
    setSelectedCheckInTicketId(null);
  }, [selectedBookingId]);

  return (
    <div className="legacy-page agenda-page">
      <section className="agenda-day-card">
        <div className="agenda-day-header">
          <div>
            <p className="section-tag">Dia de jogo</p>
            <h2>Agenda de {formatDate(selectedDate)}</h2>
            <p className="agenda-day-subtitle">
              Horarios operacionais das 07:00 as 19:00, carregados pelo endpoint agregado do dia.
            </p>
          </div>
          <div className="agenda-date-controls">
            <div className="agenda-date-field">
              <CalendarDays className="h-4 w-4 text-slate-500" />
              <Input
                aria-label="Data da agenda"
                className="h-11 border-0 bg-transparent p-0 text-slate-900 shadow-none focus-visible:ring-0"
                type="date"
                value={selectedDate}
                onChange={(event) => setSelectedDate(event.target.value)}
              />
            </div>
            <Button
              className="h-11 bg-white text-slate-700 shadow-sm hover:bg-slate-100"
              type="button"
              variant="outline"
              onClick={() => setSelectedDate(todayIsoDate())}
            >
              Hoje
            </Button>
            <Button
              className="h-11 bg-[#052d5f] text-white hover:bg-[#073a73]"
              disabled={isLoading}
              type="button"
              onClick={loadAgenda}
            >
              <RefreshCw className={isLoading ? "h-4 w-4 animate-spin" : "h-4 w-4"} />
              {isLoading ? "Atualizando..." : "Atualizar"}
            </Button>
          </div>
        </div>

        <p className={`feedback agenda-feedback ${feedback.type}`.trim()}>{feedback.message}</p>

        <div className="agenda-kpi-grid">
          <article>
            <span>Tee times</span>
            <strong>{dailyTeeTimes.length}</strong>
            <small>criados no dia</small>
          </article>
          <article>
            <span>Bookings</span>
            <strong>{dailyBookings.length}</strong>
            <small>ativos no dia</small>
          </article>
          <article>
            <span>Horarios</span>
            <strong>{slots.length}</strong>
            <small>slots possiveis</small>
          </article>
          <article>
            <span>Jogadores</span>
            <strong>{dailyTeeTimes.reduce((total, teeTime) => total + Number(teeTime.bookedPlayers || 0), 0)}</strong>
            <small>reservados</small>
          </article>
        </div>

        <div className="agenda-slots-surface">
          <div className="agenda-tee-sheet" role="table" aria-label="Tee sheet do dia">
            {agendaSlots.map((slot) => {
              const rowState = getTeeSheetRowState(slot);
              const rowLabel = getTeeSheetRowLabel(slot);
              const cells = getTeeSheetBookingCells(slot);
              const emptyCellCount = getEmptyTeeSheetCellCount(slot, cells);
              const isSelectedSlot = slot.bookings.some((booking) => booking.id === selectedBookingId);

              return (
                <div className={`agenda-tee-row ${rowState} ${isSelectedSlot ? "selected" : ""}`.trim()} key={slot.time} role="row">
                  <button
                    className="agenda-tee-time"
                    disabled={isLoading}
                    type="button"
                    onClick={() => {
                      void handleSlotClick(slot);
                    }}
                  >
                    <strong>{slot.time}</strong>
                    <span>{rowLabel}</span>
                    {slot.teeTime ? (
                      <small>
                        {slot.teeTime.bookedPlayers}/{slot.teeTime.maxPlayers}
                      </small>
                    ) : (
                      <small>Novo</small>
                    )}
                  </button>

                  <div className="agenda-tee-cells" role="cell">
                    {rowState === "blocked" ? (
                      <button
                        className="agenda-tee-cell blocked span-4"
                        disabled={isLoading}
                        type="button"
                        onClick={() => {
                          void handleSlotClick(slot);
                        }}
                      >
                        <strong>Horario bloqueado</strong>
                        <span>{slot.teeTime?.status || "BLOCKED"}</span>
                      </button>
                    ) : (
                      <>
                        {cells.map((cell) => (
                          <button
                            className={`agenda-tee-cell ${cell.state}`.trim()}
                            disabled={isLoading}
                            key={`${cell.booking.id}-${cell.bookingPlayer.id}`}
                            style={{ gridColumn: `span ${cell.count}` }}
                            type="button"
                            onClick={() => {
                              void handleSlotClick(slot);
                            }}
                          >
                            <strong>{cell.label}</strong>
                            <span>{cell.meta}</span>
                            {cell.rentalIcons.length > 0 ? (
                              <span className="agenda-tee-rental-icons" aria-label="Materiais alugados">
                                {cell.rentalIcons.map((icon) => (
                                  <span className={`agenda-tee-rental-icon ${icon.type}`} key={icon.key} title={icon.label}>
                                    {getRentalBadgeText(icon)}
                                  </span>
                                ))}
                              </span>
                            ) : null}
                          </button>
                        ))}

                        {Array.from({ length: emptyCellCount }).map((_, index) => (
                          <button
                            className="agenda-tee-cell free"
                            disabled={isLoading}
                            key={`${slot.time}-free-${index}`}
                            type="button"
                            onClick={() => {
                              void handleSlotClick(slot);
                            }}
                          >
                            <strong>Livre</strong>
                            <span>{formatMoney(slot.teeTime?.baseGreenFee)}</span>
                          </button>
                        ))}
                      </>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      <Sheet open={isBookingSheetOpen} onOpenChange={setIsBookingSheetOpen}>
        <SheetContent className="booking-operation-sheet w-[min(96vw,1080px)] overflow-y-auto sm:max-w-none" side="right">
          <SheetHeader className="booking-operation-sheet-header">
            <div>
              <p className="section-tag">Booking selecionado</p>
              <SheetTitle>
                {selectedBooking ? selectedBooking.code || `Booking #${selectedBooking.id}` : "Nenhum booking selecionado"}
              </SheetTitle>
              <SheetDescription>
                {selectedBooking && selectedTeeTime
                  ? `${formatDate(selectedTeeTime.playDate)} as ${formatTime(selectedTeeTime.startTime)}`
                  : "Clique em um horario da tee sheet para criar ou selecionar um booking."}
              </SheetDescription>
            </div>
            {selectedBooking ? (
              renderStatusBadge(selectedBooking.status)
            ) : null}
          </SheetHeader>

          <div className="booking-operation-sheet-body">

        {selectedBooking && selectedTeeTime ? (
          <>
            <div className="booking-detail-tabs" role="tablist" aria-label="Detalhes do booking">
              <button
                className={activeDetailTab === "summary" ? "active" : ""}
                type="button"
                onClick={() => setActiveDetailTab("summary")}
              >
                Resumo
              </button>
              <button
                className={activeDetailTab === "players" ? "active" : ""}
                type="button"
                onClick={() => setActiveDetailTab("players")}
              >
                Jogadores
              </button>
              <button
                className={activeDetailTab === "rentals" ? "active" : ""}
                type="button"
                onClick={() => setActiveDetailTab("rentals")}
              >
                Materiais
              </button>
              <button
                className={activeDetailTab === "payments" ? "active" : ""}
                type="button"
                onClick={() => setActiveDetailTab("payments")}
              >
                Pagamentos
              </button>
              <button
                className={activeDetailTab === "receipts" ? "active" : ""}
                type="button"
                onClick={() => setActiveDetailTab("receipts")}
              >
                Recibos
              </button>
            </div>

            {activeDetailTab === "summary" ? (
              <>
                <div className="booking-summary-status">
                  <div>
                    <span className="detail-label">Status automatico</span>
                    <strong>{selectedBooking.status}</strong>
                    <span>
                      {isSelectedBookingReady
                        ? "Todos os jogadores estao com check-in e pagamento completo."
                        : "O backend sincroniza o status conforme check-ins e pagamentos."}
                    </span>
                  </div>
                  <div>
                    <span className="detail-label">Horario</span>
                    <strong>
                      {formatDate(selectedTeeTime.playDate)} as {formatTime(selectedTeeTime.startTime)}
                    </strong>
                    <span>Booking #{selectedBooking.id}</span>
                  </div>
                </div>

                <div className="booking-detail-grid">
                  <div>
                    <span className="detail-label">Jogadores</span>
                    <strong>
                      {selectedPlayerCount}/{selectedTeeTime.maxPlayers}
                    </strong>
                  </div>
                  <div>
                    <span className="detail-label">Check-ins</span>
                    <strong>
                      {selectedCheckedInCount}/{selectedPlayerCount}
                    </strong>
                  </div>
                  <div>
                    <span className="detail-label">Rentals</span>
                    <strong>{selectedRentalTransactions.length}</strong>
                    <span>
                      {selectedActiveRentalCount} ativos, {selectedReturnedRentalCount} devolvidos
                    </span>
                  </div>
                  <div>
                    <span className="detail-label">Pagamentos</span>
                    <strong>{selectedPayments.length}</strong>
                    <span>
                      {selectedPaidPaymentCount} pagos, {selectedRefundedPaymentCount} reembolsados
                    </span>
                  </div>
                  <div>
                    <span className="detail-label">Green fee</span>
                    <strong>{formatMoney(selectedGreenFeeTotal)}</strong>
                  </div>
                  <div>
                    <span className="detail-label">Materiais</span>
                    <strong>{formatMoney(selectedRentalTotal)}</strong>
                  </div>
                  <div>
                    <span className="detail-label">Pago</span>
                    <strong>{formatMoney(selectedPaidTotal)}</strong>
                  </div>
                  <div>
                    <span className="detail-label">Pendente</span>
                    <strong>{formatMoney(selectedPendingTotal)}</strong>
                  </div>
                  <div>
                    <span className="detail-label">Total booking</span>
                    <strong>{formatMoney(selectedBooking.totalAmount)}</strong>
                  </div>
                  {selectedBooking.createdBy ? (
                    <div>
                      <span className="detail-label">Criado por</span>
                      <strong>Usuario #{selectedBooking.createdBy}</strong>
                    </div>
                  ) : null}
                </div>

                <div className="summary-player-list">
                  <div className="summary-section-header">
                    <div>
                      <span className="detail-label">Resumo por jogador</span>
                      <strong>{selectedPlayerCount} jogador(es)</strong>
                    </div>
                  </div>

                  {selectedBookingPlayers.length === 0 ? (
                    <p className="empty-state">Nenhum jogador adicionado a este booking.</p>
                  ) : (
                    selectedBookingPlayers.map((bookingPlayer) => {
                      const rentalTotal = getBookingPlayerRentalTotal(bookingPlayer.id);
                      const paidAmount = getBookingPlayerPaidAmount(bookingPlayer.id);
                      const pendingAmount = getBookingPlayerPendingAmount(bookingPlayer.id);

                      return (
                        <div className="summary-player-row" key={bookingPlayer.id}>
                          <div>
                            <strong>{getPlayerName(bookingPlayer.playerId)}</strong>
                            <span>{getBookingPlayerCount(bookingPlayer)} jogador(es) nesta linha</span>
                            <span>{bookingPlayer.checkedIn ? "Check-in feito" : "Check-in pendente"}</span>
                          </div>
                          <div>
                            <span>
                              Green fee {formatMoney(bookingPlayer.greenFeeAmount)} x {getBookingPlayerCount(bookingPlayer)}
                            </span>
                            <span>Rental {formatMoney(rentalTotal)}</span>
                          </div>
                          <div>
                            <span>Pago {formatMoney(paidAmount)}</span>
                            <span>Pendente {formatMoney(pendingAmount)}</span>
                          </div>
                        </div>
                      );
                    })
                  )}
                </div>
              </>
            ) : null}

            {activeDetailTab === "players" ? (
              <>
                <form className="booking-player-form" onSubmit={handleAddBookingPlayer}>
                  <label>
                    <span>Player</span>
                    <select
                      value={selectedPlayerId}
                      onChange={(event) => setSelectedPlayerId(event.target.value)}
                    >
                      <option value="">Selecione um player</option>
                      {sortedPlayers.map((player) => (
                        <option key={player.id} value={player.id}>
                          {player.fullName} - #{player.id}
                        </option>
                      ))}
                    </select>
                  </label>
                  <label>
                    <span>Green fee opcional</span>
                    <input
                      min="0"
                      placeholder="Automatico"
                      step="0.01"
                      type="number"
                      value={newBookingPlayerGreenFee}
                      onChange={(event) => setNewBookingPlayerGreenFee(event.target.value)}
                    />
                  </label>
                  <label>
                    <span>Qtd jogadores</span>
                    <input
                      max="4"
                      min="1"
                      step="1"
                      type="number"
                      value={newBookingPlayerCount}
                      onChange={(event) => setNewBookingPlayerCount(event.target.value)}
                    />
                  </label>
                  <label className="checkbox-field compact-checkbox">
                    <input
                      checked={newBookingPlayerCheckedIn}
                      type="checkbox"
                      onChange={(event) => setNewBookingPlayerCheckedIn(event.target.checked)}
                    />
                    <span>Entrar ja com check-in</span>
                  </label>
                  <button className="primary-button" disabled={isLoading || !selectedBooking?.id} type="submit">
                    Adicionar jogador
                  </button>
                </form>
                {bookingPlayerFeedback.message ? (
                  <p className={`feedback inline-feedback ${bookingPlayerFeedback.type}`.trim()}>
                    {bookingPlayerFeedback.message}
                  </p>
                ) : null}

                <div className="booking-player-totals">
                  <span>Green fee {formatMoney(selectedGreenFeeTotal)}</span>
                  <span>Rental {formatMoney(selectedRentalTotal)}</span>
                  <span>Pago {formatMoney(selectedPaidTotal)}</span>
                  <span>Pendente {formatMoney(selectedPendingTotal)}</span>
                </div>

                <div className="detail-list">
                  {selectedBookingPlayers.length === 0 ? (
                    <p className="empty-state">Nenhum jogador adicionado a este booking.</p>
                  ) : (
                    selectedBookingPlayers.map((bookingPlayer) => {
                      const rentalTotal = getBookingPlayerRentalTotal(bookingPlayer.id);
                      const playerTotal = getBookingPlayerTotal(bookingPlayer);
                      const paidAmount = getBookingPlayerPaidAmount(bookingPlayer.id);
                      const pendingAmount = getBookingPlayerPendingAmount(bookingPlayer.id);
                      const checkInTicket = getActiveCheckInTicketForBookingPlayer(bookingPlayer.id);

                      return (
                        <div className="detail-list-row booking-player-row" key={bookingPlayer.id}>
                          <div>
                            <strong>{getPlayerName(bookingPlayer.playerId)}</strong>
                            <span>
                              Player #{bookingPlayer.playerId} - Booking player #{bookingPlayer.id}
                            </span>
                            <span>
                              {getPlayerName(bookingPlayer.playerId)} x {getBookingPlayerCount(bookingPlayer)} player(s)
                            </span>
                          </div>
                          <div>
                            <span>
                              Green fee {formatMoney(bookingPlayer.greenFeeAmount)} x {getBookingPlayerCount(bookingPlayer)}
                            </span>
                            <span>Rental {formatMoney(rentalTotal)}</span>
                            <span>Total {formatMoney(playerTotal)}</span>
                            <span>Pago {formatMoney(paidAmount)}</span>
                            <span>Pendente {formatMoney(pendingAmount)}</span>
                          </div>
                          {renderStatusBadge(bookingPlayer.checkedIn ? "CHECK-IN" : "PENDENTE")}
                          <div className="table-actions">
                            <button
                              className="action-button edit"
                              disabled={isLoading}
                              type="button"
                              onClick={() => {
                                void handleToggleBookingPlayerCheckIn(bookingPlayer);
                              }}
                            >
                              {bookingPlayer.checkedIn ? "Desfazer check-in" : "Check-in"}
                            </button>
                            <button
                              className="action-button select"
                              disabled={isLoading || !bookingPlayer.checkedIn}
                              type="button"
                              onClick={() => {
                                void handleShowCheckInTicket(bookingPlayer);
                              }}
                            >
                              {checkInTicket && !checkInTicket.cancelled ? "Ticket" : "Gerar ticket"}
                            </button>
                            {canDelete ? (
                              <button
                                className="action-button delete"
                                disabled={isLoading}
                                type="button"
                                onClick={() => requestConfirmation({
                                  title: "Remover jogador do booking?",
                                  description: `${getPlayerName(bookingPlayer.playerId)} sera removido deste horario.`,
                                  confirmLabel: "Remover",
                                  variant: "destructive",
                                  action: () => handleRemoveBookingPlayer(bookingPlayer)
                                })}
                              >
                                Remover
                              </button>
                            ) : null}
                          </div>
                        </div>
                      );
                    })
                  )}
                </div>

                {selectedCheckInTicket ? (
                  <section className="receipt-preview-panel" id="check-in-ticket-print-area">
                    <div className="receipt-toolbar no-print">
                      <div>
                        <p className="section-tag">Ticket de check-in</p>
                        <h3>{selectedCheckInTicket.ticketNumber || `Ticket #${selectedCheckInTicket.id}`}</h3>
                      </div>
                      <div className="table-actions">
                        <button className="action-button select" type="button" onClick={handlePrintCheckInTicket}>
                          Imprimir
                        </button>
                        <button
                          className="action-button edit"
                          type="button"
                          onClick={() => {
                            setSelectedCheckInTicketId(null);
                          }}
                        >
                          Fechar
                        </button>
                      </div>
                    </div>

                    <div className="receipt-paper check-in-ticket-paper">
                      <div className="receipt-header">
                        <div>
                          <p className="receipt-kicker">Golf Office</p>
                          <h3>{selectedCheckInTicket.ticketNumber || `#${selectedCheckInTicket.id}`}</h3>
                        </div>
                        <span className={`receipt-status ${selectedCheckInTicket.cancelled ? "cancelled" : ""}`.trim()}>
                          {getCheckInTicketStatus(selectedCheckInTicket)}
                        </span>
                      </div>

                      <div className="receipt-meta">
                        <span>Player</span>
                        <strong>
                          {selectedCheckInTicket.playerNameSnapshot || getBookingPlayerDisplayName(selectedCheckInTicket.bookingPlayerId)}
                        </strong>
                        <span>Jogadores</span>
                        <strong>{getCheckInTicketPlayerCount(selectedCheckInTicket)} player(s)</strong>
                        <span>Recibo</span>
                        <strong>
                          {getReceiptForBookingPlayer(selectedCheckInTicket.bookingPlayerId)?.receiptNumber || "Sem recibo emitido"}
                        </strong>
                        <span>Data</span>
                        <strong>{selectedCheckInTicket.playDate ? formatDate(selectedCheckInTicket.playDate) : "Sem data"}</strong>
                        <span>Emitido em</span>
                        <strong>{formatDateTime(selectedCheckInTicket.issuedAt)}</strong>
                      </div>

                      <div className="ticket-route-grid">
                        <div>
                          <span>Tee inicial</span>
                          <strong>{selectedCheckInTicket.startingTee || "TEE 1"}</strong>
                          <strong>{formatTime(selectedCheckInTicket.startTime || undefined)}</strong>
                        </div>
                        <div>
                          <span>Cruzamento</span>
                          <strong>{selectedCheckInTicket.crossingTee || "TEE 10"}</strong>
                          <strong>{formatTime(selectedCheckInTicket.crossingTime || undefined)}</strong>
                        </div>
                      </div>

                      {selectedCheckInTicket.cancelled ? (
                        <p className="receipt-cancel-note">
                          Cancelado em {formatDateTime(selectedCheckInTicket.cancelledAt)}.
                          {selectedCheckInTicket.cancellationReason ? ` Motivo: ${selectedCheckInTicket.cancellationReason}` : ""}
                        </p>
                      ) : null}
                    </div>
                  </section>
                ) : null}
              </>
            ) : null}

            {activeDetailTab === "rentals" ? (
              <>
                <form className="rental-transaction-form" onSubmit={handleRentalTransactionSubmit}>
                  <label>
                    <span>Jogador</span>
                    <select
                      value={selectedRentalBookingPlayerId}
                      onChange={(event) => setSelectedRentalBookingPlayerId(event.target.value)}
                    >
                      <option value="">Selecione um jogador</option>
                      {selectedBookingPlayers.map((bookingPlayer) => (
                        <option key={bookingPlayer.id} value={bookingPlayer.id}>
                          {getPlayerName(bookingPlayer.playerId)} x {getBookingPlayerCount(bookingPlayer)} - BP #{bookingPlayer.id}
                        </option>
                      ))}
                    </select>
                  </label>
                  <label>
                    <span>Material</span>
                    <select
                      value={selectedRentalItemId}
                      onChange={(event) => setSelectedRentalItemId(event.target.value)}
                    >
                      <option value="">Selecione um material</option>
                      {activeRentalItems.map((rentalItem) => {
                        const availableStock = getAvailableStockForRentalForm(rentalItem.id);
                        return (
                          <option
                            disabled={!editingRentalTransactionId && availableStock < 1}
                            key={rentalItem.id}
                            value={rentalItem.id}
                          >
                            {rentalItem.name} - disponivel {availableStock} - {formatMoney(rentalItem.rentalPrice)}
                          </option>
                        );
                      })}
                    </select>
                  </label>
                  <label>
                    <span>Quantidade</span>
                    <input
                      min="1"
                      max={selectedRentalItemId ? getAvailableStockForRentalForm(Number(selectedRentalItemId)) : undefined}
                      step="1"
                      type="number"
                      value={rentalQuantity}
                      onChange={(event) => setRentalQuantity(event.target.value)}
                    />
                  </label>
                  <label>
                    <span>Status</span>
                    <select value={rentalStatus} onChange={(event) => setRentalStatus(event.target.value)}>
                      <option value="RENTED">RENTED</option>
                      <option value="RETURNED">RETURNED</option>
                      <option value="CANCELLED">CANCELLED</option>
                      <option value="LOST">LOST</option>
                      <option value="DAMAGED">DAMAGED</option>
                    </select>
                  </label>
                  <div className="rental-form-actions">
                    <button
                      className="primary-button"
                      disabled={isLoading || selectedBookingPlayers.length === 0}
                      type="submit"
                    >
                      {editingRentalTransactionId ? "Salvar rental" : "Adicionar material"}
                    </button>
                    {editingRentalTransactionId ? (
                      <button className="ghost-button" type="button" onClick={resetRentalTransactionForm}>
                        Cancelar edicao
                      </button>
                    ) : null}
                  </div>
                </form>

                <div className="booking-player-totals">
                  <span>Total rental {formatMoney(selectedRentalTotal)}</span>
                  <span>{selectedRentalTransactions.length} rental(s)</span>
                  <span>{rentalItems.length} item(ns) no inventario</span>
                </div>

                <div className="detail-list">
                  {selectedRentalTransactions.length === 0 ? (
                    <p className="empty-state">Nenhum material alugado neste booking.</p>
                  ) : (
                    selectedRentalTransactions.map((rentalTransaction) => {
                      const status = String(rentalTransaction.status || "RENTED").toUpperCase();
                      const canReturn = status === "RENTED";
                      const canDeleteRentalTransaction = !isRentalStockReserved(status);

                      return (
                        <div className="rental-transaction-card" key={rentalTransaction.id}>
                          <div className="detail-list-row rental-transaction-row">
                            <div>
                              <strong>{getRentalItemName(rentalTransaction.rentalItemId)}</strong>
                              <span>Item #{rentalTransaction.rentalItemId}</span>
                              <span>{getBookingPlayerDisplayName(rentalTransaction.bookingPlayerId)}</span>
                            </div>
                            <div>
                              <span>{rentalTransaction.quantity} unidade(s)</span>
                              <span>Unitario {formatMoney(rentalTransaction.unitPrice)}</span>
                              <span>Total {formatMoney(rentalTransaction.totalPrice)}</span>
                            </div>
                            {renderStatusBadge(status)}
                            <div className="table-actions">
                              {canManageFinancial ? (
                                <button
                                  className="action-button edit"
                                  disabled={isLoading}
                                  type="button"
                                  onClick={() => handleEditRentalTransaction(rentalTransaction)}
                                >
                                  Editar
                                </button>
                              ) : null}
                              <button
                                className="action-button select"
                                disabled={isLoading || !canReturn}
                                type="button"
                                onClick={() => startReturnInspection(rentalTransaction)}
                              >
                                Devolver
                              </button>
                              {canDelete ? (
                                <button
                                  className="action-button delete"
                                  disabled={isLoading || !canDeleteRentalTransaction}
                                  type="button"
                                  onClick={() => requestConfirmation({
                                    title: "Excluir rental?",
                                    description: `${getRentalItemName(rentalTransaction.rentalItemId)} sera removido deste booking.`,
                                    confirmLabel: "Excluir",
                                    variant: "destructive",
                                    action: () => handleDeleteRentalTransaction(rentalTransaction)
                                  })}
                                >
                                  Excluir
                                </button>
                              ) : null}
                            </div>
                          </div>

                          {returnInspectionRentalId === Number(rentalTransaction.id) ? (
                            <div className="return-inspection-panel">
                              <div>
                                <span className="detail-label">Inspecao de retorno</span>
                                <strong>{getRentalItemName(rentalTransaction.rentalItemId)}</strong>
                                <p>Confirme se o material voltou em bom estado ou registre a avaria encontrada.</p>
                              </div>
                              <label>
                                <span>Unidade ou etiqueta</span>
                                <input
                                  maxLength={80}
                                  placeholder="Ex.: Buggy #4, trolley T-12"
                                  type="text"
                                  value={returnDamageUnitLabel}
                                  onChange={(event) => setReturnDamageUnitLabel(event.target.value)}
                                />
                              </label>
                              <label>
                                <span>Avaria observada</span>
                                <textarea
                                  maxLength={500}
                                  placeholder="Ex.: trolley voltou com roda danificada."
                                  rows={3}
                                  value={returnDamageDescription}
                                  onChange={(event) => setReturnDamageDescription(event.target.value)}
                                />
                              </label>
                              <div className="return-inspection-actions">
                                <button
                                  className="primary-button"
                                  disabled={isLoading}
                                  type="button"
                                  onClick={() => {
                                    void handleReturnRentalTransaction(rentalTransaction, "RETURNED");
                                  }}
                                >
                                  Sem avaria
                                </button>
                                <button
                                  className="action-button delete"
                                  disabled={isLoading || !returnDamageDescription.trim()}
                                  type="button"
                                  onClick={() => {
                                    void handleReturnRentalTransaction(rentalTransaction, "DAMAGED");
                                  }}
                                >
                                  Registrar avaria
                                </button>
                                <button className="ghost-button" disabled={isLoading} type="button" onClick={resetReturnInspection}>
                                  Cancelar
                                </button>
                              </div>
                            </div>
                          ) : null}
                        </div>
                      );
                    })
                  )}
                </div>
              </>
            ) : null}

            {activeDetailTab === "payments" ? (
              <>
                <form className="payment-form" onSubmit={handlePaymentSubmit}>
                  <label>
                    <span>Jogador</span>
                    <select
                      value={selectedPaymentBookingPlayerId}
                      onChange={(event) => {
                        const bookingPlayerId = Number(event.target.value);
                        setSelectedPaymentBookingPlayerId(event.target.value);
                        setPaymentAmount(
                          bookingPlayerId ? getBookingPlayerPendingAmount(bookingPlayerId, editingPaymentId).toFixed(2) : ""
                        );
                      }}
                    >
                      <option value="">Selecione um jogador</option>
                      {selectedBookingPlayers.map((bookingPlayer) => (
                        <option key={bookingPlayer.id} value={bookingPlayer.id}>
                          {getPlayerName(bookingPlayer.playerId)} x {getBookingPlayerCount(bookingPlayer)} - pendente{" "}
                          {formatMoney(getBookingPlayerPendingAmount(bookingPlayer.id, editingPaymentId))}
                        </option>
                      ))}
                    </select>
                  </label>
                  <label>
                    <span>Valor</span>
                    <input
                      min="0.01"
                      max={selectedPaymentBookingPlayerId ? getPaymentFormMaxAmount() : undefined}
                      step="0.01"
                      type="number"
                      value={paymentAmount}
                      onChange={(event) => setPaymentAmount(event.target.value)}
                    />
                  </label>
                  <label>
                    <span>Metodo</span>
                    <select value={paymentMethod} onChange={(event) => setPaymentMethod(event.target.value)}>
                      <option value="CARD">CARD</option>
                      <option value="CASH">CASH</option>
                      <option value="MBWAY">MBWAY</option>
                      <option value="TRANSFER">TRANSFER</option>
                    </select>
                  </label>
                  <label>
                    <span>Status</span>
                    <select value={paymentStatus} onChange={(event) => setPaymentStatus(event.target.value)}>
                      <option value="PAID">PAID</option>
                      <option value="PENDING">PENDING</option>
                      <option value="REFUNDED">REFUNDED</option>
                      <option value="CANCELLED">CANCELLED</option>
                    </select>
                  </label>
                  <div className="payment-form-actions">
                    <button
                      className="primary-button"
                      disabled={isLoading || selectedBookingPlayers.length === 0}
                      type="submit"
                    >
                      {editingPaymentId ? "Salvar pagamento" : "Registrar pagamento"}
                    </button>
                    {editingPaymentId ? (
                      <button className="ghost-button" type="button" onClick={resetPaymentForm}>
                        Cancelar edicao
                      </button>
                    ) : null}
                  </div>
                </form>

                <div className="booking-player-totals">
                  <span>Total booking {formatMoney(selectedBooking.totalAmount)}</span>
                  <span>Pago {formatMoney(selectedPaidTotal)}</span>
                  <span>Pendente {formatMoney(selectedPendingTotal)}</span>
                  <span>{selectedPayments.length} pagamento(s)</span>
                  <span>{selectedReceipts.length} recibo(s)</span>
                </div>
                {receiptFeedback.message ? (
                  <p className={`feedback inline-feedback ${receiptFeedback.type}`.trim()}>
                    {receiptFeedback.message}
                  </p>
                ) : null}

                <div className="detail-list">
                  {selectedPayments.length === 0 ? (
                    <p className="empty-state">Nenhum pagamento registrado neste booking.</p>
                  ) : (
                    selectedPayments.map((payment) => {
                      const status = String(payment.status || "PAID").toUpperCase();
                      const canRefund = status === "PAID";
                      const receipt = getReceiptForPayment(payment.id);
                      const canOpenReceipt = Boolean(receipt?.id) || status === "PAID";

                      return (
                        <div className="detail-list-row payment-row" key={payment.id}>
                          <div>
                            <strong>{formatMoney(payment.amount)}</strong>
                            <span>{getBookingPlayerDisplayName(payment.bookingPlayerId)}</span>
                            <span>Booking player #{payment.bookingPlayerId}</span>
                          </div>
                          <div>
                            <span>{payment.method}</span>
                            <span>{formatDateTime(payment.paidAt)}</span>
                            <span>Pendente atual {formatMoney(getBookingPlayerPendingAmount(payment.bookingPlayerId))}</span>
                            <span>Recibo {getReceiptStatus(receipt)}</span>
                          </div>
                          {renderStatusBadge(status)}
                          <div className="table-actions">
                            {canManageFinancial ? (
                              <button
                                className="action-button edit"
                                disabled={isLoading}
                                type="button"
                                onClick={() => handleEditPayment(payment)}
                              >
                                Editar
                              </button>
                            ) : null}
                            {canManageFinancial ? (
                              <button
                                className="action-button select"
                                disabled={isLoading || !canRefund}
                                type="button"
                                onClick={() => requestConfirmation({
                                  title: "Reembolsar pagamento?",
                                  description: `O pagamento de ${formatMoney(payment.amount)} sera marcado como reembolsado.`,
                                  confirmLabel: "Reembolsar",
                                  variant: "destructive",
                                  action: () => handleRefundPayment(payment)
                                })}
                              >
                                Reembolsar
                              </button>
                            ) : null}
                            <button
                              className="action-button select"
                              disabled={isLoading || !canOpenReceipt}
                              type="button"
                              onClick={() => {
                                void handleOpenReceipt(payment);
                              }}
                            >
                              {receipt ? "Recibo" : "Emitir recibo"}
                            </button>
                            {canDelete ? (
                              <button
                                className="action-button delete"
                                disabled={isLoading}
                                type="button"
                                onClick={() => requestConfirmation({
                                  title: "Excluir pagamento?",
                                  description: `O pagamento de ${formatMoney(payment.amount)} sera removido do booking.`,
                                  confirmLabel: "Excluir",
                                  variant: "destructive",
                                  action: () => handleDeletePayment(payment)
                                })}
                              >
                                Excluir
                              </button>
                            ) : null}
                          </div>
                        </div>
                      );
                    })
                  )}
                </div>

                {selectedReceipt ? (
                  <section className="receipt-preview-panel" id="receipt-print-area">
                    <div className="receipt-toolbar no-print">
                      <div>
                        <p className="section-tag">Recibo</p>
                        <h3>{selectedReceipt.receiptNumber || `Receipt #${selectedReceipt.id}`}</h3>
                      </div>
                      <div className="table-actions">
                        <button className="action-button select" type="button" onClick={handlePrintReceipt}>
                          Imprimir
                        </button>
                        {!selectedReceipt.cancelled && canCancelReceipt ? (
                          <button
                            className="action-button delete"
                            disabled={isLoading}
                            type="button"
                            onClick={() => requestConfirmation({
                              title: "Cancelar recibo?",
                              description: `${selectedReceipt.receiptNumber || `Recibo #${selectedReceipt.id}`} sera mantido no historico como cancelado.`,
                              confirmLabel: "Cancelar recibo",
                              variant: "destructive",
                              action: () => handleCancelReceipt(selectedReceipt)
                            })}
                          >
                            Cancelar recibo
                          </button>
                        ) : null}
                        <button
                          className="action-button edit"
                          type="button"
                          onClick={() => {
                            setSelectedReceiptId(null);
                          }}
                        >
                          Fechar
                        </button>
                      </div>
                    </div>

                    <div className="receipt-paper">
                      <div className="receipt-header">
                        <div>
                          <p className="receipt-kicker">Golf Office</p>
                          <h3>{selectedReceipt.receiptNumber || `#${selectedReceipt.id}`}</h3>
                        </div>
                        <span className={`receipt-status ${selectedReceipt.cancelled ? "cancelled" : ""}`.trim()}>
                          {getReceiptStatus(selectedReceipt)}
                        </span>
                      </div>

                      <div className="receipt-meta">
                        <span>Player</span>
                        <strong>{selectedReceipt.playerNameSnapshot || getBookingPlayerDisplayName(selectedReceipt.bookingPlayerId)}</strong>
                        <span>Tax number</span>
                        <strong>{selectedReceipt.playerTaxNumberSnapshot || "Sem numero fiscal"}</strong>
                        <span>Booking</span>
                        <strong>{selectedReceipt.bookingCodeSnapshot || `Booking #${selectedReceipt.bookingId}`}</strong>
                        <span>Tee time</span>
                        <strong>
                          {selectedReceipt.playDate ? formatDate(selectedReceipt.playDate) : "Sem data"}{" "}
                          {formatTime(selectedReceipt.startTime || undefined)}
                        </strong>
                        <span>Issued at</span>
                        <strong>{formatDateTime(selectedReceipt.issuedAt)}</strong>
                        <span>Method</span>
                        <strong>{selectedReceipt.paymentMethod || "Sem metodo"}</strong>
                      </div>

                      <div className="receipt-items">
                        <div className="receipt-item-row header">
                          <span>Item</span>
                          <span>Qtd</span>
                          <span>Unitario</span>
                          <span>Total</span>
                        </div>
                        {selectedReceiptItems.length === 0 ? (
                          <p className="empty-state">Nenhum item encontrado para este recibo.</p>
                        ) : (
                          selectedReceiptItems.map((item) => (
                            <div className="receipt-item-row" key={item.id}>
                              <span>{item.description}</span>
                              <span>{item.quantity}</span>
                              <span>{formatMoney(item.unitPrice)}</span>
                              <strong>{formatMoney(item.totalPrice)}</strong>
                            </div>
                          ))
                        )}
                      </div>

                      <div className="receipt-total-line">
                        <span>Green fee</span>
                        <strong>{formatMoney(selectedReceipt.greenFeeAmount)}</strong>
                      </div>
                      <div className="receipt-total-line">
                        <span>Rental</span>
                        <strong>{formatMoney(selectedReceipt.rentalAmount)}</strong>
                      </div>
                      <div className="receipt-total-line grand-total">
                        <span>Total</span>
                        <strong>{formatMoney(selectedReceipt.totalAmount)}</strong>
                      </div>

                      {selectedReceipt.cancelled ? (
                        <p className="receipt-cancel-note">
                          Cancelado em {formatDateTime(selectedReceipt.cancelledAt)}.
                          {selectedReceipt.cancellationReason ? ` Motivo: ${selectedReceipt.cancellationReason}` : ""}
                        </p>
                      ) : null}
                    </div>
                  </section>
                ) : null}
              </>
            ) : null}

            {activeDetailTab === "receipts" ? (
              <>
                <div className="booking-player-totals">
                  <span>{selectedReceipts.length} recibo(s)</span>
                  <span>
                    Emitidos {selectedReceipts.filter((receipt) => !receipt.cancelled).length}
                  </span>
                  <span>
                    Cancelados {selectedReceipts.filter((receipt) => receipt.cancelled).length}
                  </span>
                </div>

                <div className="detail-list">
                  {selectedReceipts.length === 0 ? (
                    <p className="empty-state">Nenhum recibo emitido para este booking.</p>
                  ) : (
                    selectedReceipts.map((receipt) => (
                      <div className="detail-list-row payment-row" key={receipt.id}>
                        <div>
                          <strong>{receipt.receiptNumber || `Recibo #${receipt.id}`}</strong>
                          <span>{receipt.playerNameSnapshot || getBookingPlayerDisplayName(receipt.bookingPlayerId)}</span>
                          <span>Tax number {receipt.playerTaxNumberSnapshot || "Sem numero fiscal"}</span>
                        </div>
                        <div>
                          <span>Total {formatMoney(receipt.totalAmount)}</span>
                          <span>{formatDateTime(receipt.issuedAt)}</span>
                          <span>{receipt.paymentMethod || "Sem metodo"}</span>
                        </div>
                        {renderStatusBadge(getReceiptStatus(receipt))}
                        <div className="table-actions">
                          <button
                            className="action-button select"
                            type="button"
                            onClick={() => {
                              setSelectedReceiptId(receipt.id ?? null);
                              setActiveDetailTab("payments");
                            }}
                          >
                            Abrir
                          </button>
                          {!receipt.cancelled && canCancelReceipt ? (
                            <button
                              className="action-button delete"
                              disabled={isLoading}
                              type="button"
                              onClick={() => requestConfirmation({
                                title: "Cancelar recibo?",
                                description: `${receipt.receiptNumber || `Recibo #${receipt.id}`} sera mantido no historico como cancelado.`,
                                confirmLabel: "Cancelar recibo",
                                variant: "destructive",
                                action: () => handleCancelReceipt(receipt)
                              })}
                            >
                              Cancelar
                            </button>
                          ) : null}
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </>
            ) : null}
          </>
        ) : (
          <p className="empty-state">Clique em um horario para criar ou selecionar um booking.</p>
        )}
          </div>
        </SheetContent>
      </Sheet>

      <section className="json-grid">
        <article className="json-card">
          <p className="json-label">Ultima requisicao</p>
          <pre>{requestJson}</pre>
        </article>
        <article className="json-card">
          <p className="json-label">Ultima resposta</p>
          <pre>{responseJson}</pre>
        </article>
      </section>

      <Dialog open={Boolean(confirmation)} onOpenChange={(open) => !open && setConfirmation(null)}>
        <DialogContent className="border-slate-200 bg-white text-slate-950">
          <DialogHeader>
            <DialogTitle>{confirmation?.title || "Confirmar acao"}</DialogTitle>
            <DialogDescription className="text-slate-600">
              {confirmation?.description || "Confirme para continuar."}
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button
              className="bg-white text-slate-700 hover:bg-slate-100"
              disabled={isLoading}
              type="button"
              variant="outline"
              onClick={() => setConfirmation(null)}
            >
              Voltar
            </Button>
            <Button
              className={
                confirmation?.variant === "destructive"
                  ? "bg-red-600 text-white hover:bg-red-700"
                  : "bg-[#052d5f] text-white hover:bg-[#073a73]"
              }
              disabled={isLoading}
              type="button"
              onClick={() => {
                void handleConfirmAction();
              }}
            >
              {confirmation?.confirmLabel || "Confirmar"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}

