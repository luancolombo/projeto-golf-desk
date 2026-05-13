package com.project.golfofficeapi.mapper.custom;

import com.project.golfofficeapi.dto.BookingDTO;
import com.project.golfofficeapi.enums.BookingStatus;
import com.project.golfofficeapi.model.Booking;
import com.project.golfofficeapi.model.TeeTime;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingMapper {

    public Booking toEntity(BookingDTO dto, TeeTime teeTime) {
        Booking entity = new Booking();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setStatus(BookingStatus.fromString(dto.getStatus()));
        entity.setTotalAmount(dto.getTotalAmount());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setTeeTime(teeTime);
        return entity;
    }

    public BookingDTO toDTO(Booking entity) {
        BookingDTO dto = new BookingDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setStatus(entity.getStatus() == null ? null : entity.getStatus().name());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setTeeTimeId(entity.getTeeTimeId());
        return dto;
    }

    public List<BookingDTO> toDTOList(List<Booking> bookings) {
        return bookings.stream()
                .map(this::toDTO)
                .toList();
    }
}
