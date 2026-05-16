package com.project.golfofficeapi.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AgendaDayDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private LocalDate date;
    private List<PlayerDTO> players = new ArrayList<>();
    private List<RentalItemDTO> rentalItems = new ArrayList<>();
    private List<TeeTimeDTO> teeTimes = new ArrayList<>();
    private List<BookingDTO> bookings = new ArrayList<>();
    private List<BookingPlayerDTO> bookingPlayers = new ArrayList<>();
    private List<RentalTransactionDTO> rentalTransactions = new ArrayList<>();
    private List<PaymentDTO> payments = new ArrayList<>();
    private List<ReceiptDTO> receipts = new ArrayList<>();
    private List<ReceiptItemDTO> receiptItems = new ArrayList<>();
    private List<CheckInTicketDTO> checkInTickets = new ArrayList<>();

    public AgendaDayDTO() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<PlayerDTO> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerDTO> players) {
        this.players = players;
    }

    public List<RentalItemDTO> getRentalItems() {
        return rentalItems;
    }

    public void setRentalItems(List<RentalItemDTO> rentalItems) {
        this.rentalItems = rentalItems;
    }

    public List<TeeTimeDTO> getTeeTimes() {
        return teeTimes;
    }

    public void setTeeTimes(List<TeeTimeDTO> teeTimes) {
        this.teeTimes = teeTimes;
    }

    public List<BookingDTO> getBookings() {
        return bookings;
    }

    public void setBookings(List<BookingDTO> bookings) {
        this.bookings = bookings;
    }

    public List<BookingPlayerDTO> getBookingPlayers() {
        return bookingPlayers;
    }

    public void setBookingPlayers(List<BookingPlayerDTO> bookingPlayers) {
        this.bookingPlayers = bookingPlayers;
    }

    public List<RentalTransactionDTO> getRentalTransactions() {
        return rentalTransactions;
    }

    public void setRentalTransactions(List<RentalTransactionDTO> rentalTransactions) {
        this.rentalTransactions = rentalTransactions;
    }

    public List<PaymentDTO> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentDTO> payments) {
        this.payments = payments;
    }

    public List<ReceiptDTO> getReceipts() {
        return receipts;
    }

    public void setReceipts(List<ReceiptDTO> receipts) {
        this.receipts = receipts;
    }

    public List<ReceiptItemDTO> getReceiptItems() {
        return receiptItems;
    }

    public void setReceiptItems(List<ReceiptItemDTO> receiptItems) {
        this.receiptItems = receiptItems;
    }

    public List<CheckInTicketDTO> getCheckInTickets() {
        return checkInTickets;
    }

    public void setCheckInTickets(List<CheckInTicketDTO> checkInTickets) {
        this.checkInTickets = checkInTickets;
    }
}
