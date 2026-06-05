package com.project.golfofficeapi.services;

import com.project.golfofficeapi.dto.CashRegisterClosureItemDTO;
import com.project.golfofficeapi.enums.CashRegisterClosureStatus;
import com.project.golfofficeapi.exceptions.BusinessException;
import com.project.golfofficeapi.exceptions.RequiredObjectIsNullException;
import com.project.golfofficeapi.exceptions.ResourceNotFoundException;
import com.project.golfofficeapi.mapper.custom.CashRegisterClosureItemMapper;
import com.project.golfofficeapi.model.CashRegisterClosure;
import com.project.golfofficeapi.model.CashRegisterClosureItem;
import com.project.golfofficeapi.repository.CashRegisterClosureItemRepository;
import com.project.golfofficeapi.repository.CashRegisterClosureRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@Service
public class CashRegisterClosureItemService {

    private final CashRegisterClosureItemRepository repository;
    private final CashRegisterClosureRepository closureRepository;
    private final CashRegisterClosureItemMapper mapper;
    private final Logger logger = Logger.getLogger(CashRegisterClosureItemService.class.getName());

    public CashRegisterClosureItemService(
            CashRegisterClosureItemRepository repository,
            CashRegisterClosureRepository closureRepository,
            CashRegisterClosureItemMapper mapper
    ) {
        this.repository = repository;
        this.closureRepository = closureRepository;
        this.mapper = mapper;
    }

    public Page<CashRegisterClosureItemDTO> findAll(Pageable pageable) {
        logger.info("Find All Cash Register Closure Items");
        return repository.findAll(pageable).map(mapper::toDTO);
    }

    public CashRegisterClosureItemDTO findById(Long id) {
        logger.info("Find Cash Register Closure Item by ID");
        CashRegisterClosureItem item = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cash register closure item not found"));
        return mapper.toDTO(item);
    }

    public Page<CashRegisterClosureItemDTO> findByCashRegisterClosureId(Long cashRegisterClosureId, Pageable pageable) {
        logger.info("Find Cash Register Closure Items by Closure ID");
        findClosure(cashRegisterClosureId);
        return repository.findByCashRegisterClosure_Id(cashRegisterClosureId, pageable).map(mapper::toDTO);
    }

    @Transactional
    public CashRegisterClosureItemDTO create(CashRegisterClosureItemDTO item) {
        if (item == null) throw new RequiredObjectIsNullException();
        logger.info("Create Cash Register Closure Item");
        CashRegisterClosure closure = validateEditableClosure(item.getCashRegisterClosureId());
        prepareItem(item);

        CashRegisterClosureItem savedItem = repository.save(mapper.toEntity(item, closure));
        return mapper.toDTO(savedItem);
    }

    @Transactional
    public CashRegisterClosureItemDTO update(CashRegisterClosureItemDTO item) {
        if (item == null) throw new RequiredObjectIsNullException();
        if (item.getId() == null) throw new BusinessException("Cash register closure item id is required");
        logger.info("Update Cash Register Closure Item");

        CashRegisterClosureItem entity = repository.findById(item.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cash register closure item not found"));
        CashRegisterClosure closure = validateEditableClosure(item.getCashRegisterClosureId());
        prepareItem(item);

        entity.setCashRegisterClosure(closure);
        entity.setType(mapper.toEntity(item, closure).getType());
        entity.setReferenceId(item.getReferenceId());
        entity.setReferenceCode(item.getReferenceCode());
        entity.setDescription(item.getDescription().trim());
        entity.setAmount(item.getAmount());
        entity.setPaymentMethod(mapper.toEntity(item, closure).getPaymentMethod());
        entity.setPaymentStatus(mapper.toEntity(item, closure).getPaymentStatus());
        entity.setOccurredAt(item.getOccurredAt());

        return mapper.toDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Delete Cash Register Closure Item");
        CashRegisterClosureItem item = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cash register closure item not found"));
        validateEditableClosure(item.getCashRegisterClosureId());
        repository.delete(item);
    }

    private CashRegisterClosure findClosure(Long closureId) {
        return closureRepository.findById(closureId)
                .orElseThrow(() -> new ResourceNotFoundException("Cash register closure not found"));
    }

    private CashRegisterClosure validateEditableClosure(Long closureId) {
        CashRegisterClosure closure = findClosure(closureId);

        if (closure.getStatus() != CashRegisterClosureStatus.OPEN) {
            throw new BusinessException("Only open cash register closures can have items changed");
        }

        return closure;
    }

    private void prepareItem(CashRegisterClosureItemDTO item) {
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new BusinessException("Description is required");
        }

        if (item.getOccurredAt() == null) {
            item.setOccurredAt(LocalDateTime.now());
        }

        item.setDescription(item.getDescription().trim());
    }
}
