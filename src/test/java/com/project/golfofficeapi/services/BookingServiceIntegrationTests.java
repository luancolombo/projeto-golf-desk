package com.project.golfofficeapi.services;

import com.project.golfofficeapi.dto.BookingDTO;
import com.project.golfofficeapi.dto.TeeTimeDTO;
import com.project.golfofficeapi.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class BookingServiceIntegrationTests {

    private final BookingService bookingService;
    private final TeeTimeService teeTimeService;

    @Autowired
    BookingServiceIntegrationTests(BookingService bookingService, TeeTimeService teeTimeService) {
        this.bookingService = bookingService;
        this.teeTimeService = teeTimeService;
    }

    @Test
    void shouldCreateUpdateListAndDeleteBookingForAgendaSlot() {
        TeeTimeDTO teeTime = teeTimeService.create(newTeeTime());

        BookingDTO created = bookingService.create(newBooking(teeTime.getId()));

        assertThat(created.getId()).isNotNull();
        assertThat(created.getCode()).startsWith("BK-");
        assertThat(created.getCreatedAt()).isNotNull();
        assertThat(created.getStatus()).isEqualTo("CREATED");
        assertThat(created.getTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(created.getCreatedBy()).isNull();
        assertThat(created.getTeeTimeId()).isEqualTo(teeTime.getId());

        BookingDTO found = bookingService.findById(created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getTeeTimeId()).isEqualTo(teeTime.getId());

        assertThat(bookingService.findAll())
                .extracting(BookingDTO::getId)
                .contains(created.getId());

        found.setStatus("CONFIRMED");
        found.setTotalAmount(new BigDecimal("25.00"));

        BookingDTO updated = bookingService.update(found);

        assertThat(updated.getId()).isEqualTo(created.getId());
        assertThat(updated.getCode()).isEqualTo(created.getCode());
        assertThat(updated.getStatus()).isEqualTo("CONFIRMED");
        assertThat(updated.getTotalAmount()).isEqualByComparingTo("25.00");
        assertThat(updated.getTeeTimeId()).isEqualTo(teeTime.getId());

        bookingService.delete(updated.getId());

        assertThatThrownBy(() -> bookingService.findById(updated.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Booking not found");
    }

    private TeeTimeDTO newTeeTime() {
        int randomDays = ThreadLocalRandom.current().nextInt(2001, 4000);
        int randomMinutes = ThreadLocalRandom.current().nextInt(0, 60);

        TeeTimeDTO teeTime = new TeeTimeDTO();
        teeTime.setPlayDate(LocalDate.now().plusYears(5).plusDays(randomDays));
        teeTime.setStartTime(LocalTime.of(9, randomMinutes));
        teeTime.setMaxPlayers(4);
        teeTime.setBookedPlayers(0);
        teeTime.setStatus("AVAILABLE");
        teeTime.setBaseGreenFee(BigDecimal.ZERO);
        return teeTime;
    }

    private BookingDTO newBooking(Long teeTimeId) {
        BookingDTO booking = new BookingDTO();
        booking.setCode(null);
        booking.setStatus("CREATED");
        booking.setTotalAmount(null);
        booking.setCreatedBy(null);
        booking.setTeeTimeId(teeTimeId);
        return booking;
    }
}
