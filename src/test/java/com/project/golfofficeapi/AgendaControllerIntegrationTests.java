package com.project.golfofficeapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.golfofficeapi.dto.BookingDTO;
import com.project.golfofficeapi.dto.BookingPlayerDTO;
import com.project.golfofficeapi.dto.PaymentDTO;
import com.project.golfofficeapi.dto.PlayerDTO;
import com.project.golfofficeapi.dto.ReceiptDTO;
import com.project.golfofficeapi.dto.RentalItemDTO;
import com.project.golfofficeapi.dto.RentalTransactionDTO;
import com.project.golfofficeapi.dto.TeeTimeDTO;
import com.project.golfofficeapi.repository.CheckInTicketRepository;
import com.project.golfofficeapi.repository.ReceiptItemRepository;
import com.project.golfofficeapi.services.BookingPlayerService;
import com.project.golfofficeapi.services.BookingService;
import com.project.golfofficeapi.services.PaymentService;
import com.project.golfofficeapi.services.PlayerService;
import com.project.golfofficeapi.services.ReceiptService;
import com.project.golfofficeapi.services.RentalItemService;
import com.project.golfofficeapi.services.RentalTransactionService;
import com.project.golfofficeapi.services.TeeTimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AgendaControllerIntegrationTests {

    private static final String MANAGER_EMAIL = "manager@golfoffice.dev";
    private static final String MANAGER_PASSWORD = "admin123";

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final TeeTimeService teeTimeService;
    private final BookingService bookingService;
    private final PlayerService playerService;
    private final BookingPlayerService bookingPlayerService;
    private final RentalItemService rentalItemService;
    private final RentalTransactionService rentalTransactionService;
    private final PaymentService paymentService;
    private final ReceiptService receiptService;
    private final ReceiptItemRepository receiptItemRepository;
    private final CheckInTicketRepository checkInTicketRepository;

    @Autowired
    AgendaControllerIntegrationTests(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            TeeTimeService teeTimeService,
            BookingService bookingService,
            PlayerService playerService,
            BookingPlayerService bookingPlayerService,
            RentalItemService rentalItemService,
            RentalTransactionService rentalTransactionService,
            PaymentService paymentService,
            ReceiptService receiptService,
            ReceiptItemRepository receiptItemRepository,
            CheckInTicketRepository checkInTicketRepository
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.teeTimeService = teeTimeService;
        this.bookingService = bookingService;
        this.playerService = playerService;
        this.bookingPlayerService = bookingPlayerService;
        this.rentalItemService = rentalItemService;
        this.rentalTransactionService = rentalTransactionService;
        this.paymentService = paymentService;
        this.receiptService = receiptService;
        this.receiptItemRepository = receiptItemRepository;
        this.checkInTicketRepository = checkInTicketRepository;
    }

    @Test
    void shouldReturnOnlyRequestedDateDataWithAgendaRelationships() throws Exception {
        LocalDate targetDate = randomFutureDate();
        LocalDate otherDate = targetDate.plusDays(1);

        AgendaFixture target = createAgendaFixture(targetDate, LocalTime.of(9, 10));
        AgendaFixture other = createAgendaFixture(otherDate, LocalTime.of(9, 20));

        String token = loginAndExtractAccessToken();

        String response = mockMvc.perform(get("/agenda/day")
                        .param("date", targetDate.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(targetDate.toString()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode agenda = objectMapper.readTree(response);

        assertThat(containsId(agenda.get("teeTimes"), target.teeTime().getId())).isTrue();
        assertThat(containsId(agenda.get("teeTimes"), other.teeTime().getId())).isFalse();
        assertThat(allDateFieldsMatch(agenda.get("teeTimes"), "playDate", targetDate)).isTrue();

        assertThat(containsId(agenda.get("bookings"), target.booking().getId())).isTrue();
        assertThat(containsId(agenda.get("bookings"), other.booking().getId())).isFalse();
        assertThat(findById(agenda.get("bookings"), target.booking().getId()).get("teeTimeId").asLong())
                .isEqualTo(target.teeTime().getId());

        assertThat(containsId(agenda.get("bookingPlayers"), target.bookingPlayer().getId())).isTrue();
        assertThat(containsId(agenda.get("bookingPlayers"), other.bookingPlayer().getId())).isFalse();
        JsonNode targetBookingPlayer = findById(agenda.get("bookingPlayers"), target.bookingPlayer().getId());
        assertThat(targetBookingPlayer.get("bookingId").asLong()).isEqualTo(target.booking().getId());
        assertThat(targetBookingPlayer.get("playerId").asLong()).isEqualTo(target.player().getId());
        assertThat(targetBookingPlayer.get("playerCount").asInt()).isEqualTo(2);
        assertThat(targetBookingPlayer.get("checkedIn").asBoolean()).isTrue();

        assertThat(containsId(agenda.get("rentalTransactions"), target.rentalTransaction().getId())).isTrue();
        assertThat(containsId(agenda.get("rentalTransactions"), other.rentalTransaction().getId())).isFalse();
        JsonNode targetRental = findById(agenda.get("rentalTransactions"), target.rentalTransaction().getId());
        assertThat(targetRental.get("bookingId").asLong()).isEqualTo(target.booking().getId());
        assertThat(targetRental.get("bookingPlayerId").asLong()).isEqualTo(target.bookingPlayer().getId());
        assertThat(targetRental.get("rentalItemId").asLong()).isEqualTo(target.rentalItem().getId());

        assertThat(containsId(agenda.get("payments"), target.payment().getId())).isTrue();
        assertThat(containsId(agenda.get("payments"), other.payment().getId())).isFalse();
        JsonNode targetPayment = findById(agenda.get("payments"), target.payment().getId());
        assertThat(targetPayment.get("bookingId").asLong()).isEqualTo(target.booking().getId());
        assertThat(targetPayment.get("bookingPlayerId").asLong()).isEqualTo(target.bookingPlayer().getId());

        assertThat(containsId(agenda.get("receipts"), target.receipt().getId())).isTrue();
        assertThat(containsId(agenda.get("receipts"), other.receipt().getId())).isFalse();
        assertThat(allDateFieldsMatch(agenda.get("receipts"), "playDate", targetDate)).isTrue();
        JsonNode targetReceipt = findById(agenda.get("receipts"), target.receipt().getId());
        assertThat(targetReceipt.get("bookingId").asLong()).isEqualTo(target.booking().getId());
        assertThat(targetReceipt.get("bookingPlayerId").asLong()).isEqualTo(target.bookingPlayer().getId());
        assertThat(targetReceipt.get("paymentId").asLong()).isEqualTo(target.payment().getId());

        assertThat(containsId(agenda.get("receiptItems"), target.receiptItemId())).isTrue();
        assertThat(containsId(agenda.get("receiptItems"), other.receiptItemId())).isFalse();
        assertThat(findById(agenda.get("receiptItems"), target.receiptItemId()).get("receiptId").asLong())
                .isEqualTo(target.receipt().getId());

        assertThat(containsId(agenda.get("checkInTickets"), target.checkInTicketId())).isTrue();
        assertThat(containsId(agenda.get("checkInTickets"), other.checkInTicketId())).isFalse();
        assertThat(allDateFieldsMatch(agenda.get("checkInTickets"), "playDate", targetDate)).isTrue();
        JsonNode targetTicket = findById(agenda.get("checkInTickets"), target.checkInTicketId());
        assertThat(targetTicket.get("bookingPlayerId").asLong()).isEqualTo(target.bookingPlayer().getId());
        assertThat(targetTicket.get("playerCountSnapshot").asInt()).isEqualTo(2);

        assertThat(containsId(agenda.get("players"), target.player().getId())).isTrue();
        assertThat(containsId(agenda.get("rentalItems"), target.rentalItem().getId())).isTrue();
    }

    private AgendaFixture createAgendaFixture(LocalDate playDate, LocalTime startTime) {
        TeeTimeDTO teeTime = teeTimeService.create(newTeeTime(playDate, startTime));
        BookingDTO booking = bookingService.create(newBooking(teeTime.getId()));
        PlayerDTO player = playerService.create(newPlayer());
        BookingPlayerDTO bookingPlayer = bookingPlayerService.create(newBookingPlayer(booking.getId(), player.getId()));
        RentalItemDTO rentalItem = rentalItemService.create(newRentalItem());
        RentalTransactionDTO rentalTransaction = rentalTransactionService.create(newRentalTransaction(
                booking.getId(),
                bookingPlayer.getId(),
                rentalItem.getId()
        ));
        BigDecimal paymentAmount = bookingPlayer.getGreenFeeAmount()
                .multiply(BigDecimal.valueOf(bookingPlayer.getPlayerCount()))
                .add(rentalTransaction.getTotalPrice());
        PaymentDTO payment = paymentService.create(newPayment(booking.getId(), bookingPlayer.getId(), paymentAmount));
        ReceiptDTO receipt = receiptService.findByPaymentId(payment.getId()).getFirst();
        Long receiptItemId = receiptItemRepository.findByReceiptId(receipt.getId()).getFirst().getId();
        Long checkInTicketId = extractCheckInTicketId(bookingPlayer.getId());

        return new AgendaFixture(
                teeTime,
                booking,
                player,
                bookingPlayer,
                rentalItem,
                rentalTransaction,
                payment,
                receipt,
                receiptItemId,
                checkInTicketId
        );
    }

    private Long extractCheckInTicketId(Long bookingPlayerId) {
        return checkInTicketRepository.findByBookingPlayerId(bookingPlayerId).getFirst().getId();
    }

    private String loginAndExtractAccessToken() throws Exception {
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(MANAGER_EMAIL, MANAGER_PASSWORD))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken = objectMapper.readTree(response).get("accessToken").asText();
        assertThat(accessToken).isNotBlank();
        return accessToken;
    }

    private boolean containsId(JsonNode array, Long id) {
        assertThat(array).isNotNull();

        for (JsonNode item : array) {
            if (item.has("id") && item.get("id").asLong() == id) {
                return true;
            }
        }

        return false;
    }

    private JsonNode findById(JsonNode array, Long id) {
        assertThat(array).isNotNull();

        for (JsonNode item : array) {
            if (item.has("id") && item.get("id").asLong() == id) {
                return item;
            }
        }

        throw new AssertionError("Could not find JSON item with id " + id);
    }

    private boolean allDateFieldsMatch(JsonNode array, String fieldName, LocalDate expectedDate) {
        assertThat(array).isNotNull();

        for (JsonNode item : array) {
            if (!expectedDate.toString().equals(item.get(fieldName).asText())) {
                return false;
            }
        }

        return true;
    }

    private LocalDate randomFutureDate() {
        int randomDays = ThreadLocalRandom.current().nextInt(5000, 9000);
        return LocalDate.now().plusYears(10).plusDays(randomDays);
    }

    private TeeTimeDTO newTeeTime(LocalDate playDate, LocalTime startTime) {
        TeeTimeDTO teeTime = new TeeTimeDTO();
        teeTime.setPlayDate(playDate);
        teeTime.setStartTime(startTime);
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
        long uniqueNumber = ThreadLocalRandom.current().nextLong(100000000L, 999999999L);

        PlayerDTO player = new PlayerDTO();
        player.setFullName("Agenda Test Player " + uniqueNumber);
        player.setTaxNumber(String.valueOf(uniqueNumber));
        player.setEmail("agenda.test." + uniqueNumber + "@golf.test");
        player.setPhone(String.valueOf(uniqueNumber));
        player.setHandCap("18");
        player.setMember(false);
        player.setNotes("Created by agenda endpoint integration test");
        return player;
    }

    private BookingPlayerDTO newBookingPlayer(Long bookingId, Long playerId) {
        BookingPlayerDTO bookingPlayer = new BookingPlayerDTO();
        bookingPlayer.setBookingId(bookingId);
        bookingPlayer.setPlayerId(playerId);
        bookingPlayer.setGreenFeeAmount(null);
        bookingPlayer.setPlayerCount(2);
        bookingPlayer.setCheckedIn(true);
        return bookingPlayer;
    }

    private RentalItemDTO newRentalItem() {
        long uniqueNumber = ThreadLocalRandom.current().nextLong(100000000L, 999999999L);

        RentalItemDTO rentalItem = new RentalItemDTO();
        rentalItem.setName("Agenda Test Clubs " + uniqueNumber);
        rentalItem.setType("CLUBS");
        rentalItem.setTotalStock(10);
        rentalItem.setAvailableStock(10);
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

    private PaymentDTO newPayment(Long bookingId, Long bookingPlayerId, BigDecimal amount) {
        PaymentDTO payment = new PaymentDTO();
        payment.setBookingId(bookingId);
        payment.setBookingPlayerId(bookingPlayerId);
        payment.setAmount(amount);
        payment.setMethod("CARD");
        payment.setStatus("PAID");
        return payment;
    }

    private record AgendaFixture(
            TeeTimeDTO teeTime,
            BookingDTO booking,
            PlayerDTO player,
            BookingPlayerDTO bookingPlayer,
            RentalItemDTO rentalItem,
            RentalTransactionDTO rentalTransaction,
            PaymentDTO payment,
            ReceiptDTO receipt,
            Long receiptItemId,
            Long checkInTicketId
    ) {
    }

    private record LoginRequest(String email, String password) {
    }
}
