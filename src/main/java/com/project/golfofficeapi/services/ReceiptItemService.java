package com.project.golfofficeapi.services;

import com.project.golfofficeapi.dto.ReceiptItemDTO;
import com.project.golfofficeapi.exceptions.BusinessException;
import com.project.golfofficeapi.exceptions.RequiredObjectIsNullException;
import com.project.golfofficeapi.exceptions.ResourceNotFoundException;
import com.project.golfofficeapi.mapper.custom.ReceiptItemMapper;
import com.project.golfofficeapi.model.Receipt;
import com.project.golfofficeapi.model.ReceiptItem;
import com.project.golfofficeapi.repository.ReceiptItemRepository;
import com.project.golfofficeapi.repository.ReceiptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ReceiptItemService {

    private final ReceiptItemRepository repository;
    private final ReceiptRepository receiptRepository;
    private final ReceiptItemMapper mapper;
    private final Logger logger = Logger.getLogger(ReceiptItemService.class.getName());

    public ReceiptItemService(
            ReceiptItemRepository repository,
            ReceiptRepository receiptRepository,
            ReceiptItemMapper mapper
    ) {
        this.repository = repository;
        this.receiptRepository = receiptRepository;
        this.mapper = mapper;
    }

    public List<ReceiptItemDTO> findAll() {
        logger.info("Find All Receipt Items");
        return mapper.toDTOList(repository.findAll());
    }

    public ReceiptItemDTO findById(Long id) {
        logger.info("Find Receipt Item by ID");
        var receiptItem = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt item not found"));
        return mapper.toDTO(receiptItem);
    }

    public List<ReceiptItemDTO> findByReceiptId(Long receiptId) {
        logger.info("Find Receipt Items by Receipt ID");
        findReceipt(receiptId);
        return mapper.toDTOList(repository.findByReceiptId(receiptId));
    }

    @Transactional
    public ReceiptItemDTO create(ReceiptItemDTO receiptItem) {
        if (receiptItem == null) throw new RequiredObjectIsNullException();
        logger.info("Create Receipt Item");

        Receipt receipt = validateReceiptCanBeChanged(receiptItem.getReceiptId());
        prepareReceiptItem(receiptItem);
        return mapper.toDTO(repository.save(mapper.toEntity(receiptItem, receipt)));
    }

    @Transactional
    public ReceiptItemDTO update(ReceiptItemDTO receiptItem) {
        if (receiptItem == null) throw new RequiredObjectIsNullException();
        if (receiptItem.getId() == null) throw new BusinessException("Receipt item id is required");
        logger.info("Update Receipt Item");

        ReceiptItem entity = repository.findById(receiptItem.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Receipt item not found"));
        Receipt receipt = validateReceiptCanBeChanged(receiptItem.getReceiptId());
        prepareReceiptItem(receiptItem);

        entity.setReceipt(receipt);
        entity.setDescription(receiptItem.getDescription());
        entity.setQuantity(receiptItem.getQuantity());
        entity.setUnitPrice(receiptItem.getUnitPrice());
        entity.setTotalPrice(receiptItem.getTotalPrice());

        return mapper.toDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Delete Receipt Item");
        ReceiptItem entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt item not found"));
        validateReceiptCanBeChanged(entity.getReceiptId());
        throw new BusinessException("Cannot delete receipt item. Cancel the receipt instead");
    }

    private Receipt findReceipt(Long receiptId) {
        return receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found"));
    }

    private Receipt validateReceiptCanBeChanged(Long receiptId) {
        Receipt receipt = findReceipt(receiptId);

        if (Boolean.TRUE.equals(receipt.getCancelled())) {
            throw new BusinessException("Cannot change items from a cancelled receipt");
        }

        return receipt;
    }

    private void prepareReceiptItem(ReceiptItemDTO receiptItem) {
        if (receiptItem.getDescription() == null || receiptItem.getDescription().isBlank()) {
            throw new BusinessException("Description is required");
        }

        if (receiptItem.getQuantity() == null || receiptItem.getQuantity() < 1) {
            throw new BusinessException("Quantity must be at least 1");
        }

        if (receiptItem.getUnitPrice() == null || receiptItem.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Unit price cannot be negative");
        }

        receiptItem.setDescription(receiptItem.getDescription().trim());
        receiptItem.setUnitPrice(receiptItem.getUnitPrice().setScale(2, RoundingMode.HALF_UP));
        receiptItem.setTotalPrice(receiptItem.getUnitPrice()
                .multiply(BigDecimal.valueOf(receiptItem.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP));
    }
}
