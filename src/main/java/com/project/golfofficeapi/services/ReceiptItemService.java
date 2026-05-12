package com.project.golfofficeapi.services;

import com.project.golfofficeapi.controllers.ReceiptItemController;
import com.project.golfofficeapi.dto.ReceiptItemDTO;
import com.project.golfofficeapi.exceptions.BusinessException;
import com.project.golfofficeapi.exceptions.RequiredObjectIsNullException;
import com.project.golfofficeapi.exceptions.ResourceNotFoundException;
import com.project.golfofficeapi.model.Receipt;
import com.project.golfofficeapi.model.ReceiptItem;
import com.project.golfofficeapi.repository.ReceiptItemRepository;
import com.project.golfofficeapi.repository.ReceiptRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.logging.Logger;

import static com.project.golfofficeapi.mapper.ObjectMapper.parseListObject;
import static com.project.golfofficeapi.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ReceiptItemService {

    private final ReceiptItemRepository repository;
    private final ReceiptRepository receiptRepository;
    private final Logger logger = Logger.getLogger(ReceiptItemService.class.getName());

    public ReceiptItemService(ReceiptItemRepository repository, ReceiptRepository receiptRepository) {
        this.repository = repository;
        this.receiptRepository = receiptRepository;
    }

    public List<ReceiptItemDTO> findAll() {
        logger.info("Find All Receipt Items");
        var receiptItems = parseListObject(repository.findAll(), ReceiptItemDTO.class);
        receiptItems.forEach(this::addHateoasLinks);
        return receiptItems;
    }

    public ReceiptItemDTO findById(Long id) {
        logger.info("Find Receipt Item by ID");
        var receiptItem = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt item not found"));
        var dto = parseObject(receiptItem, ReceiptItemDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public List<ReceiptItemDTO> findByReceiptId(Long receiptId) {
        logger.info("Find Receipt Items by Receipt ID");
        findReceipt(receiptId);
        var receiptItems = parseListObject(repository.findByReceiptId(receiptId), ReceiptItemDTO.class);
        receiptItems.forEach(this::addHateoasLinks);
        return receiptItems;
    }

    public ReceiptItemDTO create(ReceiptItemDTO receiptItem) {
        if (receiptItem == null) throw new RequiredObjectIsNullException();
        logger.info("Create Receipt Item");

        validateReceiptCanBeChanged(receiptItem.getReceiptId());
        prepareReceiptItem(receiptItem);
        var dto = parseObject(repository.save(parseObject(receiptItem, ReceiptItem.class)), ReceiptItemDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public ReceiptItemDTO update(ReceiptItemDTO receiptItem) {
        if (receiptItem == null) throw new RequiredObjectIsNullException();
        if (receiptItem.getId() == null) throw new BusinessException("Receipt item id is required");
        logger.info("Update Receipt Item");

        ReceiptItem entity = repository.findById(receiptItem.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Receipt item not found"));
        validateReceiptCanBeChanged(receiptItem.getReceiptId());
        prepareReceiptItem(receiptItem);

        entity.setReceiptId(receiptItem.getReceiptId());
        entity.setDescription(receiptItem.getDescription());
        entity.setQuantity(receiptItem.getQuantity());
        entity.setUnitPrice(receiptItem.getUnitPrice());
        entity.setTotalPrice(receiptItem.getTotalPrice());

        var dto = parseObject(repository.save(entity), ReceiptItemDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public void delete(Long id) {
        logger.info("Delete Receipt Item");
        ReceiptItem entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt item not found"));
        validateReceiptCanBeChanged(entity.getReceiptId());
        repository.delete(entity);
    }

    private Receipt findReceipt(Long receiptId) {
        return receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found"));
    }

    private void validateReceiptCanBeChanged(Long receiptId) {
        Receipt receipt = findReceipt(receiptId);

        if (Boolean.TRUE.equals(receipt.getCancelled())) {
            throw new BusinessException("Cannot change items from a cancelled receipt");
        }
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

    private void addHateoasLinks(ReceiptItemDTO dto) {
        dto.add(linkTo(methodOn(ReceiptItemController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(ReceiptItemController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(ReceiptItemController.class).findByReceiptId(dto.getReceiptId())).withRel("findByReceipt").withType("GET"));
        dto.add(linkTo(methodOn(ReceiptItemController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(ReceiptItemController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(ReceiptItemController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
