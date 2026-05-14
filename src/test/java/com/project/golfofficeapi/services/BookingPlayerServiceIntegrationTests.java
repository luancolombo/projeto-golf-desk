package com.project.golfofficeapi.services;

import com.project.golfofficeapi.dto.BookingDTO;
import com.project.golfofficeapi.dto.BookingPlayerDTO;
import com.project.golfofficeapi.dto.CheckInTicketDTO;
import com.project.golfofficeapi.dto.PlayerDTO;
import com.project.golfofficeapi.dto.TeeTimeDTO;
import com.project.golfofficeapi.exceptions.BusinessException;
import com.project.golfofficeapi.exceptions.ResourceNotFoundException;
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
public class BookingPlayerServiceIntegrationTests {

    private final BookingPlayerService bookingPlayerService;
    private final BookingService bookingService;
    private final PlayerService playerService;
    private final TeeTimeService teeTimeService;
    private final CheckInTicketService checkInTicketService;

    @Autowired
    BookingPlayerServiceIntegrationTests(
            BookingPlayerService bookingPlayerService,
            BookingService bookingService,
            PlayerService playerService,
            TeeTimeService teeTimeService,
            CheckInTicketService checkInTicketService
    ) {
        this.bookingPlayerService = bookingPlayerService;
        this.bookingService = bookingService;
        this.playerService = playerService;
        this.teeTimeService = teeTimeService;
        this.checkInTicketService = checkInTicketService;
    }

    @Test
    void shouldCreateListUpdateAndDeleteBookingPlayerWithJpaRelationships() {
        TeeTimeDTO teeTime = teeTimeService.create(newTeeTime());
        BookingDTO booking = bookingService.create(newBooking(teeTime.getId()));
        PlayerDTO player = playerService.create(newPlayer());

        BookingPlayerDTO created = bookingPlayerService.create(newBookingPlayer(booking.getId(), player.getId()));

        assertThat(created.getId()).isNotNull();
        assertThat(created.getBookingId()).isEqualTo(booking.getId());
        assertThat(created.getPlayerId()).isEqualTo(player.getId());
        assertThat(created.getGreenFeeAmount()).isEqualByComparingTo(teeTime.getBaseGreenFee());
        assertThat(created.getPlayerCount()).isEqualTo(1);
        assertThat(created.getCheckedIn()).isFalse();

        assertThat(bookingPlayerService.findAll())
                .extracting(BookingPlayerDTO::getId)
                .contains(created.getId());

        created.setGreenFeeAmount(new BigDecimal("60.00"));
        created.setCheckedIn(true);

        BookingPlayerDTO updated = bookingPlayerService.update(created);

        assertThat(updated.getId()).isEqualTo(created.getId());
        assertThat(updated.getBookingId()).isEqualTo(booking.getId());
        assertThat(updated.getPlayerId()).isEqualTo(player.getId());
        assertThat(updated.getGreenFeeAmount()).isEqualByComparingTo("60.00");
        assertThat(updated.getCheckedIn()).isTrue();

        List<CheckInTicketDTO> issuedTickets = checkInTicketService.findByBookingPlayerId(updated.getId());
        assertThat(issuedTickets).hasSize(1);
        assertThat(issuedTickets.getFirst().getTicketNumber()).startsWith("CT-");
        assertThat(issuedTickets.getFirst().getPlayerNameSnapshot()).isEqualTo(player.getFullName());
        assertThat(issuedTickets.getFirst().getPlayerCountSnapshot()).isEqualTo(1);
        assertThat(issuedTickets.getFirst().getStartTime()).isEqualTo(teeTime.getStartTime());
        assertThat(issuedTickets.getFirst().getStartingTee()).isEqualTo("TEE 1");
        assertThat(issuedTickets.getFirst().getCrossingTee()).isEqualTo("TEE 10");
        assertThat(issuedTickets.getFirst().getCrossingTime()).isEqualTo(teeTime.getStartTime().plusHours(2).plusMinutes(15));
        assertThat(issuedTickets.getFirst().getCancelled()).isFalse();

        Long updatedId = updated.getId();

        assertThatThrownBy(() -> bookingPlayerService.delete(updatedId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot remove checked-in booking player");

        updated.setCheckedIn(false);
        updated = bookingPlayerService.update(updated);

        List<CheckInTicketDTO> cancelledTickets = checkInTicketService.findByBookingPlayerId(updatedId);
        assertThat(cancelledTickets).hasSize(1);
        assertThat(cancelledTickets.getFirst().getCancelled()).isTrue();
        assertThat(cancelledTickets.getFirst().getCancellationReason()).contains("Check-in was undone");

        assertThatThrownBy(() -> bookingPlayerService.delete(updatedId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("check-in ticket history");

        PlayerDTO secondPlayer = playerService.create(newPlayer());
        BookingPlayerDTO removable = bookingPlayerService.create(newBookingPlayer(booking.getId(), secondPlayer.getId()));
        Long removableId = removable.getId();

        bookingPlayerService.delete(removableId);

        assertThatThrownBy(() -> bookingPlayerService.findById(removableId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Booking player not found");
    }

    @Test
    void shouldAllowGroupBookingPlayerAndUsePlayerCountForCapacity() {
        TeeTimeDTO teeTime = teeTimeService.create(newTeeTime());
        BookingDTO booking = bookingService.create(newBooking(teeTime.getId()));
        PlayerDTO groupLeader = playerService.create(newPlayer());

        BookingPlayerDTO group = newBookingPlayer(booking.getId(), groupLeader.getId());
        group.setPlayerCount(4);

        BookingPlayerDTO createdGroup = bookingPlayerService.create(group);

        assertThat(createdGroup.getPlayerId()).isEqualTo(groupLeader.getId());
        assertThat(createdGroup.getPlayerCount()).isEqualTo(4);
        assertThat(bookingService.findById(booking.getId()).getTotalAmount())
                .isEqualByComparingTo(teeTime.getBaseGreenFee().multiply(new BigDecimal("4")));
        assertThat(teeTimeService.findById(teeTime.getId()).getBookedPlayers()).isEqualTo(4);
        assertThat(teeTimeService.findById(teeTime.getId()).getStatus()).isEqualTo("FULL");

        assertThatThrownBy(() -> bookingPlayerService.create(newBookingPlayer(booking.getId(), groupLeader.getId())))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Tee time is full");
    }

    @Test
    void shouldIssueCheckInTicketWithGroupPlayerCountSnapshot() {
        TeeTimeDTO teeTime = teeTimeService.create(newTeeTime());
        BookingDTO booking = bookingService.create(newBooking(teeTime.getId()));
        PlayerDTO groupLeader = playerService.create(newPlayer());
        BookingPlayerDTO group = newBookingPlayer(booking.getId(), groupLeader.getId());
        group.setPlayerCount(4);
        group.setCheckedIn(true);

        BookingPlayerDTO createdGroup = bookingPlayerService.create(group);
        List<CheckInTicketDTO> tickets = checkInTicketService.findByBookingPlayerId(createdGroup.getId());

        assertThat(tickets).hasSize(1);
        assertThat(tickets.getFirst().getPlayerNameSnapshot()).isEqualTo(groupLeader.getFullName());
        assertThat(tickets.getFirst().getPlayerCountSnapshot()).isEqualTo(4);
    }

    private TeeTimeDTO newTeeTime() {
        int randomDays = ThreadLocalRandom.current().nextInt(1, 2000);
        int randomMinutes = ThreadLocalRandom.current().nextInt(0, 60);

        TeeTimeDTO teeTime = new TeeTimeDTO();
        teeTime.setPlayDate(LocalDate.now().plusYears(5).plusDays(randomDays));
        teeTime.setStartTime(LocalTime.of(10, randomMinutes));
        teeTime.setMaxPlayers(4);
        teeTime.setBookedPlayers(0);
        teeTime.setStatus("AVAILABLE");
        teeTime.setBaseGreenFee(BigDecimal.ZERO);
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
        long uniqueNumber = ThreadLocalRandom.current().nextLong(200000000L, 899999999L);

        PlayerDTO player = new PlayerDTO();
        player.setFullName("Integration Test Player " + uniqueNumber);
        player.setTaxNumber(String.valueOf(uniqueNumber));
        player.setEmail("integration.player." + uniqueNumber + "@golf.test");
        player.setPhone(String.valueOf(uniqueNumber));
        player.setHandCap("12");
        player.setMember(true);
        player.setNotes("Created by BookingPlayer integration test");
        return player;
    }

    private BookingPlayerDTO newBookingPlayer(Long bookingId, Long playerId) {
        BookingPlayerDTO bookingPlayer = new BookingPlayerDTO();
        bookingPlayer.setBookingId(bookingId);
        bookingPlayer.setPlayerId(playerId);
        bookingPlayer.setGreenFeeAmount(null);
        bookingPlayer.setPlayerCount(null);
        bookingPlayer.setCheckedIn(false);
        return bookingPlayer;
    }
}
