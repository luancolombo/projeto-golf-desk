package com.project.golfofficeapi.mapper.custom;

import com.project.golfofficeapi.dto.CheckInTicketDTO;
import com.project.golfofficeapi.model.BookingPlayer;
import com.project.golfofficeapi.model.CheckInTicket;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CheckInTicketMapper {

    public CheckInTicket toEntity(CheckInTicketDTO dto, BookingPlayer bookingPlayer) {
        CheckInTicket entity = new CheckInTicket();
        entity.setId(dto.getId());
        entity.setTicketNumber(dto.getTicketNumber());
        entity.setBookingPlayer(bookingPlayer);
        entity.setPlayerNameSnapshot(dto.getPlayerNameSnapshot());
        entity.setPlayerCountSnapshot(dto.getPlayerCountSnapshot());
        entity.setBookingCodeSnapshot(dto.getBookingCodeSnapshot());
        entity.setPlayDate(dto.getPlayDate());
        entity.setStartTime(dto.getStartTime());
        entity.setStartingTee(dto.getStartingTee());
        entity.setCrossingTee(dto.getCrossingTee());
        entity.setCrossingTime(dto.getCrossingTime());
        entity.setIssuedAt(dto.getIssuedAt());
        entity.setCancelled(dto.getCancelled());
        entity.setCancelledAt(dto.getCancelledAt());
        entity.setCancellationReason(dto.getCancellationReason());
        return entity;
    }

    public CheckInTicketDTO toDTO(CheckInTicket entity) {
        CheckInTicketDTO dto = new CheckInTicketDTO();
        dto.setId(entity.getId());
        dto.setTicketNumber(entity.getTicketNumber());
        dto.setBookingPlayerId(entity.getBookingPlayerId());
        dto.setPlayerNameSnapshot(entity.getPlayerNameSnapshot());
        dto.setPlayerCountSnapshot(entity.getPlayerCountSnapshot());
        dto.setBookingCodeSnapshot(entity.getBookingCodeSnapshot());
        dto.setPlayDate(entity.getPlayDate());
        dto.setStartTime(entity.getStartTime());
        dto.setStartingTee(entity.getStartingTee());
        dto.setCrossingTee(entity.getCrossingTee());
        dto.setCrossingTime(entity.getCrossingTime());
        dto.setIssuedAt(entity.getIssuedAt());
        dto.setCancelled(entity.getCancelled());
        dto.setCancelledAt(entity.getCancelledAt());
        dto.setCancellationReason(entity.getCancellationReason());
        return dto;
    }

    public List<CheckInTicketDTO> toDTOList(List<CheckInTicket> tickets) {
        return tickets.stream()
                .map(this::toDTO)
                .toList();
    }
}
