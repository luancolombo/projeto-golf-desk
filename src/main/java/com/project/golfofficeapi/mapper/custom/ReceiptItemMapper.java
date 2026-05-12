package com.project.golfofficeapi.mapper.custom;

import com.project.golfofficeapi.dto.ReceiptItemDTO;
import com.project.golfofficeapi.model.Receipt;
import com.project.golfofficeapi.model.ReceiptItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReceiptItemMapper {

    public ReceiptItem toEntity(ReceiptItemDTO dto, Receipt receipt) {
        ReceiptItem entity = new ReceiptItem();
        entity.setId(dto.getId());
        entity.setReceipt(receipt);
        entity.setDescription(dto.getDescription());
        entity.setQuantity(dto.getQuantity());
        entity.setUnitPrice(dto.getUnitPrice());
        entity.setTotalPrice(dto.getTotalPrice());
        return entity;
    }

    public ReceiptItemDTO toDTO(ReceiptItem entity) {
        ReceiptItemDTO dto = new ReceiptItemDTO();
        dto.setId(entity.getId());
        dto.setReceiptId(entity.getReceiptId());
        dto.setDescription(entity.getDescription());
        dto.setQuantity(entity.getQuantity());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setTotalPrice(entity.getTotalPrice());
        return dto;
    }

    public List<ReceiptItemDTO> toDTOList(List<ReceiptItem> receiptItems) {
        return receiptItems.stream()
                .map(this::toDTO)
                .toList();
    }
}
