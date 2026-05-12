package com.project.golfofficeapi.mapper.custom;

import com.project.golfofficeapi.dto.BookingPlayerDTO;
import com.project.golfofficeapi.model.Booking;
import com.project.golfofficeapi.model.BookingPlayer;
import com.project.golfofficeapi.model.Player;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingPlayerMapper {

    public BookingPlayer toEntity(BookingPlayerDTO dto, Booking booking, Player player) {
        BookingPlayer entity = new BookingPlayer();
        entity.setId(dto.getId());
        entity.setBooking(booking);
        entity.setPlayer(player);
        entity.setGreenFeeAmount(dto.getGreenFeeAmount());
        entity.setCheckedIn(dto.getCheckedIn());
        return entity;
    }

    public BookingPlayerDTO toDTO(BookingPlayer entity) {
        BookingPlayerDTO dto = new BookingPlayerDTO();
        dto.setId(entity.getId());
        dto.setBookingId(entity.getBookingId());
        dto.setPlayerId(entity.getPlayerId());
        dto.setGreenFeeAmount(entity.getGreenFeeAmount());
        dto.setCheckedIn(entity.getCheckedIn());
        return dto;
    }

    public List<BookingPlayerDTO> toDTOList(List<BookingPlayer> bookingPlayers) {
        return bookingPlayers.stream()
                .map(this::toDTO)
                .toList();
    }
}
