package com.project.golfofficeapi.services;

import com.project.golfofficeapi.dto.BookingDTO;
import com.project.golfofficeapi.dto.BookingPlayerDTO;
import com.project.golfofficeapi.dto.PaymentDTO;
import com.project.golfofficeapi.dto.PlayerDTO;
import com.project.golfofficeapi.dto.ReceiptDTO;
import com.project.golfofficeapi.dto.RentalItemDTO;
import com.project.golfofficeapi.dto.RentalTransactionDTO;
import com.project.golfofficeapi.dto.TeeTimeDTO;
import com.project.golfofficeapi.exceptions.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class PaymentReceiptServiceIntegrationTests {

    private final TeeTimeService teeTimeService;
    private final BookingService bookingService;
    private final PlayerService playerService;
    private final BookingPlayerService bookingPlayerService;
    private final RentalItemService rentalItemService;
    private final RentalTransactionService rentalTransactionService;
    private final PaymentService paymentService;
    private final ReceiptService receiptService;

    @Autowired
    PaymentReceiptServiceIntegrationTests(
            TeeTimeService teeTimeService,
            BookingService bookingService,
            PlayerService playerService,
            BookingPlayerService bookingPlayerService,
            RentalItemService rentalItemService,
            RentalTransactionService rentalTransactionService,
            PaymentService paymentService,
            ReceiptService receiptService
    ) {
        this.teeTimeService = teeTimeService;
        this.bookingService = bookingService;
        this.playerService = playerService;
        this.bookingPlayerService = bookingPlayerService;
        this.rentalItemService = rentalItemService;
        this.rentalTransactionService = rentalTransactionService;
        this.paymentService = paymentService;
        this.receiptService = receiptService;
    }

    @Test
    void shouldHandleAgendaPaymentAndReceiptFlow() {
        TeeTimeDTO teeTime = teeTimeService.create(newTeeTime());
        BookingDTO booking = bookingService.create(newBooking(teeTime.getId()));
        PlayerDTO player = playerService.create(newPlayer());
        BookingPlayerDTO bookingPlayer = bookingPlayerService.create(newBookingPlayer(booking.getId(), player.getId()));
        RentalItemDTO rentalItem = rentalItemService.create(newRentalItem());
        RentalTransactionDTO rentalTransaction = rentalTransactionService.create(newRentalTransaction(
                booking.getId(),
                bookingPlayer.getId(),
                rentalItem.getId()
        ));

        assertThat(rentalTransaction.getTotalPrice()).isEqualByComparingTo("30.00");

        PaymentDTO payment = paymentService.create(newPayment(booking.getId(), bookingPlayer.getId(), "50.00", "CARD", "PAID"));

        assertThat(payment.getId()).isNotNull();
        assertThat(payment.getBookingId()).isEqualTo(booking.getId());
        assertThat(payment.getBookingPlayerId()).isEqualTo(bookingPlayer.getId());
        assertThat(payment.getAmount()).isEqualByComparingTo("50.00");
        assertThat(payment.getStatus()).isEqualTo("PAID");
        assertThat(payment.getPaidAt()).isNotNull();

        List<ReceiptDTO> issuedReceipts = receiptService.findByPaymentId(payment.getId());

        assertThat(issuedReceipts).hasSize(1);
        assertThat(issuedReceipts.getFirst().getReceiptNumber()).startsWith("RC-");
        assertThat(issuedReceipts.getFirst().getBookingId()).isEqualTo(booking.getId());
        assertThat(issuedReceipts.getFirst().getBookingPlayerId()).isEqualTo(bookingPlayer.getId());
        assertThat(issuedReceipts.getFirst().getPlayerNameSnapshot()).isEqualTo(player.getFullName());
        assertThat(issuedReceipts.getFirst().getPlayerTaxNumberSnapshot()).isEqualTo(player.getTaxNumber());
        assertThat(issuedReceipts.getFirst().getTotalAmount()).isEqualByComparingTo("50.00");
        assertThat(issuedReceipts.getFirst().getCancelled()).isFalse();

        assertThatThrownBy(() -> paymentService.create(newPayment(booking.getId(), bookingPlayer.getId(), "70.01", "CASH", "PAID")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Payment amount exceeds booking player pending amount");

        payment.setStatus("REFUNDED");
        PaymentDTO refundedPayment = paymentService.update(payment);

        assertThat(refundedPayment.getStatus()).isEqualTo("REFUNDED");

        List<ReceiptDTO> cancelledReceipts = receiptService.findByPaymentId(payment.getId());

        assertThat(cancelledReceipts).hasSize(1);
        assertThat(cancelledReceipts.getFirst().getCancelled()).isTrue();
        assertThat(cancelledReceipts.getFirst().getCancellationReason())
                .contains("Payment status changed to REFUNDED");
    }

    private TeeTimeDTO newTeeTime() {
        int randomDays = ThreadLocalRandom.current().nextInt(4001, 6000);
        int randomMinutes = ThreadLocalRandom.current().nextInt(0, 60);

        TeeTimeDTO teeTime = new TeeTimeDTO();
        teeTime.setPlayDate(LocalDate.now().plusYears(5).plusDays(randomDays));
        teeTime.setStartTime(LocalTime.of(11, randomMinutes));
        teeTime.setMaxPlayers(4);
        teeTime.setBookedPlayers(0);
        teeTime.setStatus("AVAILABLE");
        teeTime.setBaseGreenFee(new BigDecimal("80.00"));
        return teeTime;
    }

    private BookingDTO newBooking(Long teeTimeId) {
        BookingDTO booking = new BookingDTO();
        booking.setStatus("CREATED");
        booking.setTotalAmount(BigDecimal.ZERO);
        booking.setCreatedBy(null);
        booking.setTeeTimeId(teeTimeId);
        return booking;
    }

    private PlayerDTO newPlayer() {
        long uniqueNumber = ThreadLocalRandom.current().nextLong(100000000L, 199999999L);

        PlayerDTO player = new PlayerDTO();
        player.setFullName("Payment Receipt Test Player " + uniqueNumber);
        player.setTaxNumber(String.valueOf(uniqueNumber));
        player.setEmail("payment.receipt." + uniqueNumber + "@golf.test");
        player.setPhone(String.valueOf(uniqueNumber));
        player.setHandCap("18");
        player.setMember(false);
        player.setNotes("Created by payment receipt integration test");
        return player;
    }

    private BookingPlayerDTO newBookingPlayer(Long bookingId, Long playerId) {
        BookingPlayerDTO bookingPlayer = new BookingPlayerDTO();
        bookingPlayer.setBookingId(bookingId);
        bookingPlayer.setPlayerId(playerId);
        bookingPlayer.setGreenFeeAmount(null);
        bookingPlayer.setCheckedIn(true);
        return bookingPlayer;
    }

    private RentalItemDTO newRentalItem() {
        long uniqueNumber = ThreadLocalRandom.current().nextLong(100000000L, 199999999L);

        RentalItemDTO rentalItem = new RentalItemDTO();
        rentalItem.setName("Payment Test Clubs " + uniqueNumber);
        rentalItem.setType("CLUBS");
        rentalItem.setTotalStock(5);
        rentalItem.setAvailableStock(5);
        rentalItem.setRentalPrice(new BigDecimal("30.00"));
        rentalItem.setActive(true);
        return rentalItem;
    }

    private RentalTransactionDTO newRentalTransaction(Long bookingId, Long bookingPlayerId, Long rentalItemId) {
        RentalTransactionDTO rentalTransaction = new RentalTransactionDTO();
        rentalTransaction.setBookingId(bookingId);
        rentalTransaction.setBookingPlayerId(bookingPlayerId);
        rentalTransaction.setRentalItemId(rentalItemId);
        rentalTransaction.setQuantity(1);
        rentalTransaction.setStatus("RENTED");
        return rentalTransaction;
    }

    private PaymentDTO newPayment(
            Long bookingId,
            Long bookingPlayerId,
            String amount,
            String method,
            String status
    ) {
        PaymentDTO payment = new PaymentDTO();
        payment.setBookingId(bookingId);
        payment.setBookingPlayerId(bookingPlayerId);
        payment.setAmount(new BigDecimal(amount));
        payment.setMethod(method);
        payment.setStatus(status);
        return payment;
    }
}
