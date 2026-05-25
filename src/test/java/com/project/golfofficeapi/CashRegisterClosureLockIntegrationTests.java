package com.project.golfofficeapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.golfofficeapi.dto.BookingDTO;
import com.project.golfofficeapi.dto.BookingPlayerDTO;
import com.project.golfofficeapi.dto.PlayerDTO;
import com.project.golfofficeapi.dto.TeeTimeDTO;
import com.project.golfofficeapi.enums.CashRegisterClosureStatus;
import com.project.golfofficeapi.model.CashRegisterClosure;
import com.project.golfofficeapi.repository.CashRegisterClosureRepository;
import com.project.golfofficeapi.services.BookingPlayerService;
import com.project.golfofficeapi.services.BookingService;
import com.project.golfofficeapi.services.PlayerService;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CashRegisterClosureLockIntegrationTests {

    private static final String MANAGER_EMAIL = "manager@golfoffice.dev";
    private static final String MANAGER_PASSWORD = "admin123";
    private static final String CLOSED_AGENDA_MESSAGE = "Cannot change agenda after cash register is closed for this date";

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final CashRegisterClosureRepository cashRegisterClosureRepository;
    private final TeeTimeService teeTimeService;
    private final BookingService bookingService;
    private final PlayerService playerService;
    private final BookingPlayerService bookingPlayerService;

    @Autowired
    CashRegisterClosureLockIntegrationTests(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            CashRegisterClosureRepository cashRegisterClosureRepository,
            TeeTimeService teeTimeService,
            BookingService bookingService,
            PlayerService playerService,
            BookingPlayerService bookingPlayerService
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.cashRegisterClosureRepository = cashRegisterClosureRepository;
        this.teeTimeService = teeTimeService;
        this.bookingService = bookingService;
        this.playerService = playerService;
        this.bookingPlayerService = bookingPlayerService;
    }

    @Test
    void shouldRejectTeeTimeCreationWhenCashRegisterIsClosedForTheDate() throws Exception {
        LocalDate playDate = randomFutureDate();
        closeCashRegisterForDate(playDate);
        String token = loginAndExtractAccessToken();

        mockMvc.perform(post("/tee-time")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(teeTimePayload(playDate, LocalTime.of(9, 0))))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(CLOSED_AGENDA_MESSAGE));
    }

    @Test
    void shouldRejectBookingCreationWhenCashRegisterIsClosedForTheDate() throws Exception {
        LocalDate playDate = randomFutureDate();
        TeeTimeDTO teeTime = teeTimeService.create(newTeeTime(playDate, LocalTime.of(9, 10)));
        closeCashRegisterForDate(playDate);
        String token = loginAndExtractAccessToken();

        mockMvc.perform(post("/booking")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingPayload(teeTime.getId())))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(CLOSED_AGENDA_MESSAGE));
    }

    @Test
    void shouldRejectBookingPlayerAndPaymentCreationWhenCashRegisterIsClosedForTheDate() throws Exception {
        LocalDate playDate = randomFutureDate();
        TeeTimeDTO teeTime = teeTimeService.create(newTeeTime(playDate, LocalTime.of(9, 20)));
        BookingDTO booking = bookingService.create(newBooking(teeTime.getId()));
        PlayerDTO firstPlayer = playerService.create(newPlayer());
        PlayerDTO secondPlayer = playerService.create(newPlayer());
        BookingPlayerDTO bookingPlayer = bookingPlayerService.create(newBookingPlayer(booking.getId(), firstPlayer.getId()));

        closeCashRegisterForDate(playDate);
        String token = loginAndExtractAccessToken();

        mockMvc.perform(post("/booking-player")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingPlayerPayload(booking.getId(), secondPlayer.getId())))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(CLOSED_AGENDA_MESSAGE));

        mockMvc.perform(post("/payment")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paymentPayload(booking.getId(), bookingPlayer.getId())))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(CLOSED_AGENDA_MESSAGE));
    }

    private void closeCashRegisterForDate(LocalDate businessDate) {
        CashRegisterClosure closure = new CashRegisterClosure();
        closure.setBusinessDate(businessDate);
        closure.setOpenedAt(businessDate.atStartOfDay());
        closure.setClosedAt(LocalDateTime.now());
        closure.setStatus(CashRegisterClosureStatus.CLOSED);
        closure.setCashTotal(BigDecimal.ZERO);
        closure.setCardTotal(BigDecimal.ZERO);
        closure.setMbwayTotal(BigDecimal.ZERO);
        closure.setTransferTotal(BigDecimal.ZERO);
        closure.setGrossTotal(BigDecimal.ZERO);
        closure.setRefundedTotal(BigDecimal.ZERO);
        closure.setNetTotal(BigDecimal.ZERO);
        closure.setPaidPaymentsCount(0);
        closure.setRefundedPaymentsCount(0);
        closure.setIssuedReceiptsCount(0);
        closure.setCancelledReceiptsCount(0);
        closure.setPendingBookingsCount(0);
        closure.setUnreturnedRentalsCount(0);
        closure.setNotes("Cash register lock integration test");
        cashRegisterClosureRepository.saveAndFlush(closure);
    }

    private String loginAndExtractAccessToken() throws Exception {
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(MANAGER_EMAIL, MANAGER_PASSWORD))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("accessToken").asText();
    }

    private LocalDate randomFutureDate() {
        return LocalDate.now().plusYears(30).plusDays(ThreadLocalRandom.current().nextInt(1, 10000));
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
        booking.setTeeTimeId(teeTimeId);
        return booking;
    }

    private PlayerDTO newPlayer() {
        long uniqueNumber = ThreadLocalRandom.current().nextLong(100000000L, 999999999L);

        PlayerDTO player = new PlayerDTO();
        player.setFullName("Cash Lock Test Player " + uniqueNumber);
        player.setTaxNumber(String.valueOf(uniqueNumber));
        player.setEmail("cash.lock.test." + uniqueNumber + "@golf.test");
        player.setPhone(String.valueOf(uniqueNumber));
        player.setHandCap("18");
        player.setMember(false);
        player.setNotes("Created by cash register lock integration test");
        return player;
    }

    private BookingPlayerDTO newBookingPlayer(Long bookingId, Long playerId) {
        BookingPlayerDTO bookingPlayer = new BookingPlayerDTO();
        bookingPlayer.setBookingId(bookingId);
        bookingPlayer.setPlayerId(playerId);
        bookingPlayer.setGreenFeeAmount(null);
        bookingPlayer.setPlayerCount(1);
        bookingPlayer.setCheckedIn(false);
        return bookingPlayer;
    }

    private String teeTimePayload(LocalDate playDate, LocalTime startTime) {
        return """
                {
                  "playDate": "%s",
                  "startTime": "%s",
                  "maxPlayers": 4,
                  "bookedPlayers": 0,
                  "status": "AVAILABLE",
                  "baseGreenFee": 80.00
                }
                """.formatted(playDate, startTime);
    }

    private String bookingPayload(Long teeTimeId) {
        return """
                {
                  "status": "CREATED",
                  "totalAmount": 0.00,
                  "teeTimeId": %d
                }
                """.formatted(teeTimeId);
    }

    private String bookingPlayerPayload(Long bookingId, Long playerId) {
        return """
                {
                  "bookingId": %d,
                  "playerId": %d,
                  "greenFeeAmount": null,
                  "playerCount": 1,
                  "checkedIn": false
                }
                """.formatted(bookingId, playerId);
    }

    private String paymentPayload(Long bookingId, Long bookingPlayerId) {
        return """
                {
                  "bookingId": %d,
                  "bookingPlayerId": %d,
                  "amount": 80.00,
                  "method": "CARD",
                  "status": "PAID"
                }
                """.formatted(bookingId, bookingPlayerId);
    }

    private record LoginRequest(String email, String password) {
    }
}
