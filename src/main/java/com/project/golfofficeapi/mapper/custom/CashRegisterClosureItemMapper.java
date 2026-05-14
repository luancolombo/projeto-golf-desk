package com.project.golfofficeapi.mapper.custom;

import com.project.golfofficeapi.dto.CashRegisterClosureItemDTO;
import com.project.golfofficeapi.enums.CashRegisterClosureItemType;
import com.project.golfofficeapi.enums.PaymentMethod;
import com.project.golfofficeapi.enums.PaymentStatus;
import com.project.golfofficeapi.model.CashRegisterClosure;
import com.project.golfofficeapi.model.CashRegisterClosureItem;
import com.project.golfofficeapi.services.calculation.CashRegisterClosureItemCalculation;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CashRegisterClosureItemMapper {

    private static final String TYPE_FIELD = "type";
    private static final String PAYMENT_METHOD_FIELD = "paymentMethod";
    private static final String PAYMENT_STATUS_FIELD = "paymentStatus";

    public CashRegisterClosureItem toEntity(CashRegisterClosureItemDTO dto, CashRegisterClosure closure) {
        CashRegisterClosureItem entity = new CashRegisterClosureItem();
        entity.setId(dto.getId());
        entity.setCashRegisterClosure(closure);
        entity.setType(CashRegisterClosureItemType.fromString(dto.getType()));
        entity.setReferenceId(dto.getReferenceId());
        entity.setReferenceCode(dto.getReferenceCode());
        entity.setDescription(dto.getDescription());
        entity.setAmount(dto.getAmount());
        entity.setPaymentMethod(resolvePaymentMethod(dto.getPaymentMethod()));
        entity.setPaymentStatus(resolvePaymentStatus(dto.getPaymentStatus()));
        entity.setOccurredAt(dto.getOccurredAt());
        return entity;
    }

    public CashRegisterClosureItem toEntity(CashRegisterClosureItemCalculation calculation, CashRegisterClosure closure) {
        CashRegisterClosureItem entity = new CashRegisterClosureItem();
        BeanUtils.copyProperties(calculation, entity);
        entity.setCashRegisterClosure(closure);
        return entity;
    }

    public CashRegisterClosureItemDTO toDTO(CashRegisterClosureItem entity) {
        CashRegisterClosureItemDTO dto = new CashRegisterClosureItemDTO();
        dto.setId(entity.getId());
        dto.setCashRegisterClosureId(entity.getCashRegisterClosureId());
        dto.setType(entity.getType() == null ? null : entity.getType().name());
        dto.setReferenceId(entity.getReferenceId());
        dto.setReferenceCode(entity.getReferenceCode());
        dto.setDescription(entity.getDescription());
        dto.setAmount(entity.getAmount());
        dto.setPaymentMethod(entity.getPaymentMethod() == null ? null : entity.getPaymentMethod().name());
        dto.setPaymentStatus(entity.getPaymentStatus() == null ? null : entity.getPaymentStatus().name());
        dto.setOccurredAt(entity.getOccurredAt());
        return dto;
    }

    public CashRegisterClosureItemDTO toDTO(CashRegisterClosureItemCalculation calculation) {
        CashRegisterClosureItemDTO dto = new CashRegisterClosureItemDTO();
        BeanUtils.copyProperties(calculation, dto, TYPE_FIELD, PAYMENT_METHOD_FIELD, PAYMENT_STATUS_FIELD);
        dto.setType(calculation.getType() == null ? null : calculation.getType().name());
        dto.setPaymentMethod(calculation.getPaymentMethod() == null ? null : calculation.getPaymentMethod().name());
        dto.setPaymentStatus(calculation.getPaymentStatus() == null ? null : calculation.getPaymentStatus().name());
        return dto;
    }

    public List<CashRegisterClosureItemDTO> toDTOList(List<CashRegisterClosureItem> items) {
        return items.stream()
                .map(this::toDTO)
                .toList();
    }

    public List<CashRegisterClosureItemDTO> toCalculationDTOList(List<CashRegisterClosureItemCalculation> items) {
        return items.stream()
                .map(this::toDTO)
                .toList();
    }

    private PaymentMethod resolvePaymentMethod(String method) {
        if (method == null || method.isBlank()) {
            return null;
        }

        return PaymentMethod.fromString(method);
    }

    private PaymentStatus resolvePaymentStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }

        return PaymentStatus.fromString(status);
    }
}
