package com.project.golfofficeapi.services;

import com.project.golfofficeapi.controllers.RentalItemController;
import com.project.golfofficeapi.dto.RentalItemDTO;
import com.project.golfofficeapi.exceptions.BusinessException;
import com.project.golfofficeapi.exceptions.RequiredObjectIsNullException;
import com.project.golfofficeapi.exceptions.ResourceNotFoundException;
import com.project.golfofficeapi.model.RentalItem;
import com.project.golfofficeapi.repository.RentalItemRepository;
import com.project.golfofficeapi.repository.RentalTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

import static com.project.golfofficeapi.mapper.ObjectMapper.parseListObject;
import static com.project.golfofficeapi.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class RentalItemService {

    @Autowired
    RentalItemRepository repository;

    @Autowired
    RentalTransactionRepository rentalTransactionRepository;

    private final Logger logger = Logger.getLogger(RentalItemService.class.getName());

    public RentalItemService(RentalItemRepository repository, RentalTransactionRepository rentalTransactionRepository) {
        this.repository = repository;
        this.rentalTransactionRepository = rentalTransactionRepository;
    }

    public List<RentalItemDTO> findAll() {
        logger.info("Find All Rental Items");
        var rentalItems = parseListObject(repository.findAll(), RentalItemDTO.class);
        rentalItems.forEach(this::addHateoasLinks);
        return rentalItems;
    }

    public RentalItemDTO findById(Long id) {
        logger.info("Find Rental Item by ID");
        var rentalItem = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental item not found"));
        var dto = parseObject(rentalItem, RentalItemDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public RentalItemDTO create(RentalItemDTO rentalItem) {
        if (rentalItem == null) throw new RequiredObjectIsNullException();
        logger.info("Create Rental Item");
        prepareDefaults(rentalItem);
        validateStock(rentalItem);
        validateRentalPrice(rentalItem);

        var entity = parseObject(rentalItem, RentalItem.class);
        var dto = parseObject(repository.save(entity), RentalItemDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public RentalItemDTO update(RentalItemDTO rentalItem) {
        if (rentalItem == null) throw new RequiredObjectIsNullException();
        logger.info("Update Rental Item");
        prepareDefaults(rentalItem);
        validateStock(rentalItem);
        validateRentalPrice(rentalItem);

        RentalItem entity = repository.findById(rentalItem.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Rental item not found"));

        entity.setName(rentalItem.getName());
        entity.setType(rentalItem.getType());
        entity.setTotalStock(rentalItem.getTotalStock());
        entity.setAvailableStock(rentalItem.getAvailableStock());
        entity.setRentalPrice(rentalItem.getRentalPrice());
        entity.setActive(rentalItem.getActive());

        var dto = parseObject(repository.save(entity), RentalItemDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public void delete(Long id) {
        logger.info("Delete Rental Item");
        RentalItem entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental item not found"));

        if (rentalTransactionRepository.existsByRentalItemId(entity.getId())) {
            entity.setActive(false);
            repository.save(entity);
            return;
        }

        repository.delete(entity);
    }

    private void prepareDefaults(RentalItemDTO rentalItem) {
        if (rentalItem.getActive() == null) {
            rentalItem.setActive(true);
        }

        if (rentalItem.getAvailableStock() == null) {
            rentalItem.setAvailableStock(rentalItem.getTotalStock());
        }
    }

    private void validateStock(RentalItemDTO rentalItem) {
        if (rentalItem.getTotalStock() == null) {
            throw new BusinessException("Total stock is required");
        }

        if (rentalItem.getTotalStock() < 0) {
            throw new BusinessException("Total stock cannot be negative");
        }

        if (rentalItem.getAvailableStock() == null) {
            throw new BusinessException("Available stock is required");
        }

        if (rentalItem.getAvailableStock() < 0) {
            throw new BusinessException("Available stock cannot be negative");
        }

        if (rentalItem.getAvailableStock() > rentalItem.getTotalStock()) {
            throw new BusinessException("Available stock cannot be greater than total stock");
        }
    }

    private void validateRentalPrice(RentalItemDTO rentalItem) {
        if (rentalItem.getRentalPrice() == null) {
            throw new BusinessException("Rental price is required");
        }

        if (rentalItem.getRentalPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Rental price cannot be negative");
        }
    }

    private void addHateoasLinks(RentalItemDTO dto) {
        dto.add(linkTo(methodOn(RentalItemController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(RentalItemController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(RentalItemController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(RentalItemController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(RentalItemController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
