package com.project.golfofficeapi.mapper.custom;

import com.project.golfofficeapi.dto.CashRegisterClosureDTO;
import com.project.golfofficeapi.dto.CashRegisterClosureItemDTO;
import com.project.golfofficeapi.enums.CashRegisterClosureStatus;
import com.project.golfofficeapi.model.CashRegisterClosure;
import com.project.golfofficeapi.services.calculation.CashRegisterClosureCalculation;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CashRegisterClosureMapper {

    private static final String STATUS_FIELD = "status";
    private static final String ITEMS_FIELD = "items";

    private final CashRegisterClosureItemMapper itemMapper;

    public CashRegisterClosureMapper(CashRegisterClosureItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    public CashRegisterClosure toEntity(CashRegisterClosureDTO dto) {
        CashRegisterClosure entity = new CashRegisterClosure();
        BeanUtils.copyProperties(dto, entity, STATUS_FIELD, ITEMS_FIELD);
        entity.setStatus(CashRegisterClosureStatus.fromString(dto.getStatus()));
        return entity;
    }

    public CashRegisterClosure toEntity(CashRegisterClosureCalculation calculation) {
        CashRegisterClosure entity = new CashRegisterClosure();
        BeanUtils.copyProperties(calculation, entity, ITEMS_FIELD);
        return entity;
    }

    public CashRegisterClosureDTO toDTO(CashRegisterClosure entity) {
        CashRegisterClosureDTO dto = new CashRegisterClosureDTO();
        BeanUtils.copyProperties(entity, dto, STATUS_FIELD);
        dto.setStatus(entity.getStatus() == null ? null : entity.getStatus().name());
        return dto;
    }

    public CashRegisterClosureDTO toDTO(CashRegisterClosureCalculation calculation) {
        CashRegisterClosureDTO dto = new CashRegisterClosureDTO();
        BeanUtils.copyProperties(calculation, dto, STATUS_FIELD, ITEMS_FIELD);
        dto.setStatus(calculation.getStatus() == null ? null : calculation.getStatus().name());
        dto.setItems(itemMapper.toCalculationDTOList(calculation.getItems()));
        return dto;
    }

    public CashRegisterClosureDTO toDTO(CashRegisterClosure entity, List<CashRegisterClosureItemDTO> items) {
        CashRegisterClosureDTO dto = toDTO(entity);
        dto.setItems(items);
        return dto;
    }

    public List<CashRegisterClosureDTO> toDTOList(List<CashRegisterClosure> closures) {
        return closures.stream()
                .map(this::toDTO)
                .toList();
    }
}
