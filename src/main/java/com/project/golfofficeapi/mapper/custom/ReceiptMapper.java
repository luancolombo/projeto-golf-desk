package com.project.golfofficeapi.mapper.custom;

import com.project.golfofficeapi.dto.ReceiptDTO;
import com.project.golfofficeapi.model.Booking;
import com.project.golfofficeapi.model.BookingPlayer;
import com.project.golfofficeapi.model.Payment;
import com.project.golfofficeapi.model.Receipt;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReceiptMapper {

    public Receipt toEntity(ReceiptDTO dto, Booking booking, BookingPlayer bookingPlayer, Payment payment) {
        Receipt entity = new Receipt();
        entity.setId(dto.getId());
        entity.setReceiptNumber(dto.getReceiptNumber());
        entity.setBooking(booking);
        entity.setBookingPlayer(bookingPlayer);
        entity.setPayment(payment);
        entity.setPlayerNameSnapshot(dto.getPlayerNameSnapshot());
        entity.setPlayerTaxNumberSnapshot(dto.getPlayerTaxNumberSnapshot());
        entity.setBookingCodeSnapshot(dto.getBookingCodeSnapshot());
        entity.setPlayDate(dto.getPlayDate());
        entity.setStartTime(dto.getStartTime());
        entity.setGreenFeeAmount(dto.getGreenFeeAmount());
        entity.setRentalAmount(dto.getRentalAmount());
        entity.setTotalAmount(dto.getTotalAmount());
        entity.setPaymentMethod(dto.getPaymentMethod());
        entity.setPaymentStatus(dto.getPaymentStatus());
        entity.setIssuedAt(dto.getIssuedAt());
        entity.setCancelled(dto.getCancelled());
        entity.setCancelledAt(dto.getCancelledAt());
        entity.setCancellationReason(dto.getCancellationReason());
        return entity;
    }

    public ReceiptDTO toDTO(Receipt entity) {
        ReceiptDTO dto = new ReceiptDTO();
        dto.setId(entity.getId());
        dto.setReceiptNumber(entity.getReceiptNumber());
        dto.setBookingId(entity.getBookingId());
        dto.setBookingPlayerId(entity.getBookingPlayerId());
        dto.setPaymentId(entity.getPaymentId());
        dto.setPlayerNameSnapshot(entity.getPlayerNameSnapshot());
        dto.setPlayerTaxNumberSnapshot(entity.getPlayerTaxNumberSnapshot());
        dto.setBookingCodeSnapshot(entity.getBookingCodeSnapshot());
        dto.setPlayDate(entity.getPlayDate());
        dto.setStartTime(entity.getStartTime());
        dto.setGreenFeeAmount(entity.getGreenFeeAmount());
        dto.setRentalAmount(entity.getRentalAmount());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setPaymentMethod(entity.getPaymentMethod());
        dto.setPaymentStatus(entity.getPaymentStatus());
        dto.setIssuedAt(entity.getIssuedAt());
        dto.setCancelled(entity.getCancelled());
        dto.setCancelledAt(entity.getCancelledAt());
        dto.setCancellationReason(entity.getCancellationReason());
        return dto;
    }

    public List<ReceiptDTO> toDTOList(List<Receipt> receipts) {
        return receipts.stream()
                .map(this::toDTO)
                .toList();
    }
}
