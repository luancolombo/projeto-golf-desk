package com.project.golfofficeapi.services;

import com.project.golfofficeapi.dto.RentalDamageReportDTO;
import com.project.golfofficeapi.enums.RentalDamageReportStatus;
import com.project.golfofficeapi.enums.RentalTransactionStatus;
import com.project.golfofficeapi.exceptions.BusinessException;
import com.project.golfofficeapi.exceptions.RequiredObjectIsNullException;
import com.project.golfofficeapi.exceptions.ResourceNotFoundException;
import com.project.golfofficeapi.mapper.custom.RentalDamageReportMapper;
import com.project.golfofficeapi.model.RentalDamageReport;
import com.project.golfofficeapi.model.RentalItem;
import com.project.golfofficeapi.model.RentalTransaction;
import com.project.golfofficeapi.repository.RentalDamageReportRepository;
import com.project.golfofficeapi.repository.RentalItemRepository;
import com.project.golfofficeapi.repository.RentalTransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Service
public class RentalDamageReportService {

    private final RentalDamageReportRepository repository;
    private final RentalTransactionRepository rentalTransactionRepository;
    private final RentalItemRepository rentalItemRepository;
    private final RentalDamageReportMapper mapper;
    private final CashRegisterClosureGuardService cashRegisterClosureGuardService;
    private final Logger logger = Logger.getLogger(RentalDamageReportService.class.getName());

    public RentalDamageReportService(
            RentalDamageReportRepository repository,
            RentalTransactionRepository rentalTransactionRepository,
            RentalItemRepository rentalItemRepository,
            RentalDamageReportMapper mapper,
            CashRegisterClosureGuardService cashRegisterClosureGuardService
    ) {
        this.repository = repository;
        this.rentalTransactionRepository = rentalTransactionRepository;
        this.rentalItemRepository = rentalItemRepository;
        this.mapper = mapper;
        this.cashRegisterClosureGuardService = cashRegisterClosureGuardService;
    }

    public Page<RentalDamageReportDTO> findAll(Pageable pageable) {
        logger.info("Find All Rental Damage Reports");
        return repository.findAll(pageable).map(mapper::toDTO);
    }

    public RentalDamageReportDTO findById(Long id) {
        logger.info("Find Rental Damage Report by ID");
        RentalDamageReport report = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental damage report not found"));
        return mapper.toDTO(report);
    }

    public Page<RentalDamageReportDTO> findByStatus(String status, Pageable pageable) {
        logger.info("Find Rental Damage Reports by Status");
        return repository.findByStatus(resolveStatus(status), pageable).map(mapper::toDTO);
    }

    public Page<RentalDamageReportDTO> findByRentalItemId(Long rentalItemId, Pageable pageable) {
        logger.info("Find Rental Damage Reports by Rental Item ID");
        findRentalItem(rentalItemId);
        return repository.findByRentalItem_Id(rentalItemId, pageable).map(mapper::toDTO);
    }

    public Page<RentalDamageReportDTO> findByRentalTransactionId(Long rentalTransactionId, Pageable pageable) {
        logger.info("Find Rental Damage Reports by Rental Transaction ID");
        findRentalTransaction(rentalTransactionId);
        return repository.findByRentalTransaction_Id(rentalTransactionId, pageable).map(mapper::toDTO);
    }

    @Transactional
    public RentalDamageReportDTO create(RentalDamageReportDTO report) {
        if (report == null) throw new RequiredObjectIsNullException();
        logger.info("Create Rental Damage Report");
        validateDescription(report.getDescription());
        validateDamagedUnitLabel(report.getDamagedUnitLabel());
        prepareDefaults(report);

        RentalTransaction rentalTransaction = resolveRentalTransaction(report.getRentalTransactionId());
        if (rentalTransaction != null) {
            cashRegisterClosureGuardService.ensureRentalTransactionIsOpen(rentalTransaction);
        }
        RentalItem rentalItem = resolveRentalItem(report, rentalTransaction);

        RentalDamageReport entity = mapper.toEntity(report, rentalTransaction, rentalItem);
        return mapper.toDTO(repository.save(entity));
    }

    @Transactional
    public RentalDamageReportDTO reportTransactionDamage(Long rentalTransactionId, RentalDamageReportDTO report) {
        if (report == null) throw new RequiredObjectIsNullException();
        logger.info("Report Rental Transaction Damage");
        validateDescription(report.getDescription());
        validateDamagedUnitLabel(report.getDamagedUnitLabel());

        RentalTransaction rentalTransaction = findRentalTransaction(rentalTransactionId);
        cashRegisterClosureGuardService.ensureRentalTransactionIsOpen(rentalTransaction);

        if (rentalTransaction.getStatus() != RentalTransactionStatus.RENTED
                && rentalTransaction.getStatus() != RentalTransactionStatus.DAMAGED) {
            throw new BusinessException("Only rented or already damaged rental transactions can receive damage reports");
        }

        if (repository.existsByRentalTransaction_IdAndStatus(rentalTransactionId, RentalDamageReportStatus.OPEN)) {
            throw new BusinessException("This rental transaction already has an open damage report");
        }

        rentalTransaction.setStatus(RentalTransactionStatus.DAMAGED);
        rentalTransactionRepository.save(rentalTransaction);

        report.setRentalTransactionId(rentalTransaction.getId());
        report.setRentalItemId(rentalTransaction.getRentalItemId());
        report.setStatus(RentalDamageReportStatus.OPEN.name());
        prepareDefaults(report);

        RentalDamageReport entity = mapper.toEntity(report, rentalTransaction, rentalTransaction.getRentalItem());
        return mapper.toDTO(repository.save(entity));
    }

    @Transactional
    public RentalDamageReportDTO update(RentalDamageReportDTO report) {
        if (report == null) throw new RequiredObjectIsNullException();
        if (report.getId() == null) throw new BusinessException("Rental damage report id is required");
        logger.info("Update Rental Damage Report");
        validateDescription(report.getDescription());
        validateDamagedUnitLabel(report.getDamagedUnitLabel());

        RentalDamageReport entity = repository.findById(report.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Rental damage report not found"));

        RentalDamageReportStatus previousStatus = entity.getStatus();
        RentalDamageReportStatus status = resolveStatus(report.getStatus());
        RentalTransaction rentalTransaction = resolveRentalTransaction(report.getRentalTransactionId());
        if (rentalTransaction != null) {
            cashRegisterClosureGuardService.ensureRentalTransactionIsOpen(rentalTransaction);
        }
        RentalItem rentalItem = resolveRentalItem(report, rentalTransaction);

        entity.setRentalTransaction(rentalTransaction);
        entity.setRentalItem(rentalItem);
        entity.setDamagedUnitLabel(normalizeOptional(report.getDamagedUnitLabel()));
        entity.setDescription(report.getDescription().trim());
        entity.setStatus(status);
        entity.setReportedBy(report.getReportedBy());
        entity.setResolvedBy(report.getResolvedBy());

        if (status == RentalDamageReportStatus.RESOLVED && entity.getResolvedAt() == null) {
            entity.setResolvedAt(LocalDateTime.now());
        }

        if (status == RentalDamageReportStatus.OPEN) {
            entity.setResolvedAt(null);
            entity.setResolvedBy(null);
        }

        RentalDamageReport saved = repository.save(entity);
        if (previousStatus == RentalDamageReportStatus.OPEN && status != RentalDamageReportStatus.OPEN) {
            releaseDamagedRentalStockIfComplete(saved);
        }

        return mapper.toDTO(saved);
    }

    @Transactional
    public RentalDamageReportDTO resolve(Long id) {
        logger.info("Resolve Rental Damage Report");
        RentalDamageReport entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental damage report not found"));
        if (entity.getRentalTransaction() != null) {
            cashRegisterClosureGuardService.ensureRentalTransactionIsOpen(entity.getRentalTransaction());
        }

        if (entity.getStatus() == RentalDamageReportStatus.CANCELLED) {
            throw new BusinessException("Cancelled damage reports cannot be resolved");
        }

        entity.setStatus(RentalDamageReportStatus.RESOLVED);
        entity.setResolvedAt(LocalDateTime.now());

        RentalDamageReport saved = repository.save(entity);
        releaseDamagedRentalStockIfComplete(saved);

        return mapper.toDTO(saved);
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Cancel Rental Damage Report");
        RentalDamageReport entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental damage report not found"));
        if (entity.getRentalTransaction() != null) {
            cashRegisterClosureGuardService.ensureRentalTransactionIsOpen(entity.getRentalTransaction());
        }
        entity.setStatus(RentalDamageReportStatus.CANCELLED);
        RentalDamageReport saved = repository.save(entity);
        releaseDamagedRentalStockIfComplete(saved);
    }

    private void prepareDefaults(RentalDamageReportDTO report) {
        report.setDescription(report.getDescription().trim());

        if (report.getStatus() == null || report.getStatus().isBlank()) {
            report.setStatus(RentalDamageReportStatus.OPEN.name());
        }

        if (report.getReportedAt() == null) {
            report.setReportedAt(LocalDateTime.now());
        }

        report.setDamagedUnitLabel(normalizeOptional(report.getDamagedUnitLabel()));

        RentalDamageReportStatus status = resolveStatus(report.getStatus());

        if (status == RentalDamageReportStatus.RESOLVED && report.getResolvedAt() == null) {
            report.setResolvedAt(LocalDateTime.now());
        }
    }

    private RentalTransaction resolveRentalTransaction(Long rentalTransactionId) {
        if (rentalTransactionId == null) {
            return null;
        }

        return findRentalTransaction(rentalTransactionId);
    }

    private RentalItem resolveRentalItem(RentalDamageReportDTO report, RentalTransaction rentalTransaction) {
        if (report.getRentalItemId() != null) {
            return findRentalItem(report.getRentalItemId());
        }

        return rentalTransaction == null ? null : rentalTransaction.getRentalItem();
    }

    private RentalTransaction findRentalTransaction(Long rentalTransactionId) {
        return rentalTransactionRepository.findById(rentalTransactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental transaction not found"));
    }

    private RentalItem findRentalItem(Long rentalItemId) {
        return rentalItemRepository.findById(rentalItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental item not found"));
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new BusinessException("Damage description is required");
        }

        if (description.trim().length() > 500) {
            throw new BusinessException("Damage description cannot exceed 500 characters");
        }
    }

    private void validateDamagedUnitLabel(String damagedUnitLabel) {
        if (damagedUnitLabel != null && damagedUnitLabel.trim().length() > 80) {
            throw new BusinessException("Damaged unit label cannot exceed 80 characters");
        }
    }

    private void releaseDamagedRentalStockIfComplete(RentalDamageReport report) {
        RentalTransaction rentalTransaction = report.getRentalTransaction();

        if (rentalTransaction == null || rentalTransaction.getStatus() != RentalTransactionStatus.DAMAGED) {
            return;
        }

        long openDamageReports = repository.countByRentalTransaction_IdAndStatusAndIdNot(
                rentalTransaction.getId(),
                RentalDamageReportStatus.OPEN,
                report.getId()
        );

        if (openDamageReports > 0) {
            return;
        }

        RentalItem rentalItem = rentalTransaction.getRentalItem();
        rentalItem.setAvailableStock(Math.min(
                rentalItem.getAvailableStock() + rentalTransaction.getQuantity(),
                rentalItem.getTotalStock()
        ));
        rentalItemRepository.save(rentalItem);

        rentalTransaction.setStatus(RentalTransactionStatus.RETURNED);
        rentalTransactionRepository.save(rentalTransaction);
    }

    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private RentalDamageReportStatus resolveStatus(String status) {
        try {
            return RentalDamageReportStatus.fromString(status);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("Invalid rental damage report status");
        }
    }
}
