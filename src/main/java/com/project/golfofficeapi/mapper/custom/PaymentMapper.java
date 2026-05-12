package com.project.golfofficeapi.mapper.custom;

import com.project.golfofficeapi.dto.PaymentDTO;
import com.project.golfofficeapi.model.Booking;
import com.project.golfofficeapi.model.BookingPlayer;
import com.project.golfofficeapi.model.Payment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentMapper {

    public Payment toEntity(PaymentDTO dto, Booking booking, BookingPlayer bookingPlayer) {
        Payment entity = new Payment();
        entity.setId(dto.getId());
        entity.setBooking(booking);
        entity.setBookingPlayer(bookingPlayer);
        entity.setAmount(dto.getAmount());
        entity.setMethod(dto.getMethod());
        entity.setStatus(dto.getStatus());
        entity.setPaidAt(dto.getPaidAt());
        return entity;
    }

    public PaymentDTO toDTO(Payment entity) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(entity.getId());
        dto.setBookingId(entity.getBookingId());
        dto.setBookingPlayerId(entity.getBookingPlayerId());
        dto.setAmount(entity.getAmount());
        dto.setMethod(entity.getMethod());
        dto.setStatus(entity.getStatus());
        dto.setPaidAt(entity.getPaidAt());
        return dto;
    }

    public List<PaymentDTO> toDTOList(List<Payment> payments) {
        return payments.stream()
                .map(this::toDTO)
                .toList();
    }
}
