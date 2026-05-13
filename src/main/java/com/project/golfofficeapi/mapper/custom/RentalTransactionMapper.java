package com.project.golfofficeapi.mapper.custom;

import com.project.golfofficeapi.dto.RentalTransactionDTO;
import com.project.golfofficeapi.enums.RentalTransactionStatus;
import com.project.golfofficeapi.model.Booking;
import com.project.golfofficeapi.model.BookingPlayer;
import com.project.golfofficeapi.model.RentalItem;
import com.project.golfofficeapi.model.RentalTransaction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RentalTransactionMapper {

    public RentalTransaction toEntity(
            RentalTransactionDTO dto,
            Booking booking,
            BookingPlayer bookingPlayer,
            RentalItem rentalItem
    ) {
        RentalTransaction entity = new RentalTransaction();
        entity.setId(dto.getId());
        entity.setBooking(booking);
        entity.setBookingPlayer(bookingPlayer);
        entity.setRentalItem(rentalItem);
        entity.setQuantity(dto.getQuantity());
        entity.setStatus(RentalTransactionStatus.fromString(dto.getStatus()));
        entity.setUnitPrice(dto.getUnitPrice());
        entity.setTotalPrice(dto.getTotalPrice());
        return entity;
    }

    public RentalTransactionDTO toDTO(RentalTransaction entity) {
        RentalTransactionDTO dto = new RentalTransactionDTO();
        dto.setId(entity.getId());
        dto.setBookingId(entity.getBookingId());
        dto.setBookingPlayerId(entity.getBookingPlayerId());
        dto.setRentalItemId(entity.getRentalItemId());
        dto.setQuantity(entity.getQuantity());
        dto.setStatus(entity.getStatus() == null ? null : entity.getStatus().name());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setTotalPrice(entity.getTotalPrice());
        return dto;
    }

    public List<RentalTransactionDTO> toDTOList(List<RentalTransaction> rentalTransactions) {
        return rentalTransactions.stream()
                .map(this::toDTO)
                .toList();
    }
}
