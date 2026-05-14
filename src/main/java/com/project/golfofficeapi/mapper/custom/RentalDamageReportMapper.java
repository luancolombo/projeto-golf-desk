package com.project.golfofficeapi.mapper.custom;

import com.project.golfofficeapi.dto.RentalDamageReportDTO;
import com.project.golfofficeapi.enums.RentalDamageReportStatus;
import com.project.golfofficeapi.model.RentalDamageReport;
import com.project.golfofficeapi.model.RentalItem;
import com.project.golfofficeapi.model.RentalTransaction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RentalDamageReportMapper {

    public RentalDamageReport toEntity(
            RentalDamageReportDTO dto,
            RentalTransaction rentalTransaction,
            RentalItem rentalItem
    ) {
        RentalDamageReport entity = new RentalDamageReport();
        entity.setId(dto.getId());
        entity.setRentalTransaction(rentalTransaction);
        entity.setRentalItem(rentalItem);
        entity.setDescription(dto.getDescription());
        entity.setStatus(RentalDamageReportStatus.fromString(dto.getStatus()));
        entity.setReportedAt(dto.getReportedAt());
        entity.setResolvedAt(dto.getResolvedAt());
        entity.setReportedBy(dto.getReportedBy());
        entity.setResolvedBy(dto.getResolvedBy());
        return entity;
    }

    public RentalDamageReportDTO toDTO(RentalDamageReport entity) {
        RentalDamageReportDTO dto = new RentalDamageReportDTO();
        dto.setId(entity.getId());
        dto.setRentalTransactionId(entity.getRentalTransactionId());
        dto.setRentalItemId(entity.getRentalItemId());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus() == null ? null : entity.getStatus().name());
        dto.setReportedAt(entity.getReportedAt());
        dto.setResolvedAt(entity.getResolvedAt());
        dto.setReportedBy(entity.getReportedBy());
        dto.setResolvedBy(entity.getResolvedBy());
        return dto;
    }

    public List<RentalDamageReportDTO> toDTOList(List<RentalDamageReport> reports) {
        return reports.stream()
                .map(this::toDTO)
                .toList();
    }
}
