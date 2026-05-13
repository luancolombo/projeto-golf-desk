package com.project.golfofficeapi.mapper.custom;

import com.project.golfofficeapi.dto.TeeTimeDTO;
import com.project.golfofficeapi.enums.TeeTimeStatus;
import com.project.golfofficeapi.model.TeeTime;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeeTimeMapper {

    public TeeTime toEntity(TeeTimeDTO dto) {
        TeeTime entity = new TeeTime();
        entity.setId(dto.getId());
        entity.setPlayDate(dto.getPlayDate());
        entity.setStartTime(dto.getStartTime());
        entity.setMaxPlayers(dto.getMaxPlayers());
        entity.setBookedPlayers(dto.getBookedPlayers());
        entity.setStatus(TeeTimeStatus.fromString(dto.getStatus()));
        entity.setBaseGreenFee(dto.getBaseGreenFee());
        return entity;
    }

    public TeeTimeDTO toDTO(TeeTime entity) {
        TeeTimeDTO dto = new TeeTimeDTO();
        dto.setId(entity.getId());
        dto.setPlayDate(entity.getPlayDate());
        dto.setStartTime(entity.getStartTime());
        dto.setMaxPlayers(entity.getMaxPlayers());
        dto.setBookedPlayers(entity.getBookedPlayers());
        dto.setStatus(entity.getStatus() == null ? null : entity.getStatus().name());
        dto.setBaseGreenFee(entity.getBaseGreenFee());
        return dto;
    }

    public List<TeeTimeDTO> toDTOList(List<TeeTime> teeTimes) {
        return teeTimes.stream()
                .map(this::toDTO)
                .toList();
    }
}
