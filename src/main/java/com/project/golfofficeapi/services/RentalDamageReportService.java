package com.project.golfofficeapi.services;

import com.project.golfofficeapi.controllers.RentalDamageReportController;
import com.project.golfofficeapi.dto.RentalDamageReportDTO;
import com.project.golfofficeapi.enums.RentalDamageReportStatus;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class RentalDamageReportService {

    private final RentalDamageReportRepository repository;
    private final RentalTransactionRepository rentalTransactionRepository;
    private final RentalItemRepository rentalItemRepository;
    private final RentalDamageReportMapper mapper;
    private final Logger logger = Logger.getLogger(RentalDamageReportService.class.getName());

    public RentalDamageReportService(
            RentalDamageReportRepository repository,
            RentalTransactionRepository rentalTransactionRepository,
            RentalItemRepository rentalItemRepository,
            RentalDamageReportMapper mapper
    ) {
        this.repository = repository;
        this.rentalTransactionRepository = rentalTransactionRepository;
        this.rentalItemRepository = rentalItemRepository;
        this.mapper = mapper;
    }

    public List<RentalDamageReportDTO> findAll() {
        logger.info("Find All Rental Damage Reports");
        return toDTOListWithLinks(repository.findAll());
    }

    public RentalDamageReportDTO findById(Long id) {
        logger.info("Find Rental Damage Report by ID");
        RentalDamageReport report = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental damage report not found"));
        RentalDamageReportDTO dto = mapper.toDTO(report);
        addHateoasLinks(dto);
        return dto;
    }

    public List<RentalDamageReportDTO> findByStatus(String status) {
        logger.info("Find Rental Damage Reports by Status");
        return toDTOListWithLinks(repository.findByStatus(resolveStatus(status)));
    }

    public List<RentalDamageReportDTO> findByRentalItemId(Long rentalItemId) {
        logger.info("Find Rental Damage Reports by Rental Item ID");
        findRentalItem(rentalItemId);
        return toDTOListWithLinks(repository.findByRentalItemId(rentalItemId));
    }

    public List<RentalDamageReportDTO> findByRentalTransactionId(Long rentalTransactionId) {
        logger.info("Find Rental Damage Reports by Rental Transaction ID");
        findRentalTransaction(rentalTransactionId);
        return toDTOListWithLinks(repository.findByRentalTransactionId(rentalTransactionId));
    }

    @Transactional
    public RentalDamageReportDTO create(RentalDamageReportDTO report) {
        if (report == null) throw new RequiredObjectIsNullException();
        logger.info("Create Rental Damage Report");
        validateDescription(report.getDescription());
        prepareDefaults(report);

        RentalTransaction rentalTransaction = resolveRentalTransaction(report.getRentalTransactionId());
        RentalItem rentalItem = resolveRentalItem(report, rentalTransaction);

        RentalDamageReport entity = mapper.toEntity(report, rentalTransaction, rentalItem);
        RentalDamageReportDTO dto = mapper.toDTO(repository.save(entity));
        addHateoasLinks(dto);
        return dto;
    }

    @Transactional
    public RentalDamageReportDTO update(RentalDamageReportDTO report) {
        if (report == null) throw new RequiredObjectIsNullException();
        if (report.getId() == null) throw new BusinessException("Rental damage report id is required");
        logger.info("Update Rental Damage Report");
        validateDescription(report.getDescription());

        RentalDamageReport entity = repository.findById(report.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Rental damage report not found"));

        RentalDamageReportStatus status = resolveStatus(report.getStatus());
        RentalTransaction rentalTransaction = resolveRentalTransaction(report.getRentalTransactionId());
        RentalItem rentalItem = resolveRentalItem(report, rentalTransaction);

        entity.setRentalTransaction(rentalTransaction);
        entity.setRentalItem(rentalItem);
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

        RentalDamageReportDTO dto = mapper.toDTO(repository.save(entity));
        addHateoasLinks(dto);
        return dto;
    }

    @Transactional
    public RentalDamageReportDTO resolve(Long id) {
        logger.info("Resolve Rental Damage Report");
        RentalDamageReport entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental damage report not found"));

        if (entity.getStatus() == RentalDamageReportStatus.CANCELLED) {
            throw new BusinessException("Cancelled damage reports cannot be resolved");
        }

        entity.setStatus(RentalDamageReportStatus.RESOLVED);
        entity.setResolvedAt(LocalDateTime.now());

        RentalDamageReportDTO dto = mapper.toDTO(repository.save(entity));
        addHateoasLinks(dto);
        return dto;
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Cancel Rental Damage Report");
        RentalDamageReport entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental damage report not found"));
        entity.setStatus(RentalDamageReportStatus.CANCELLED);
        repository.save(entity);
    }

    private void prepareDefaults(RentalDamageReportDTO report) {
        report.setDescription(report.getDescription().trim());

        if (report.getStatus() == null || report.getStatus().isBlank()) {
            report.setStatus(RentalDamageReportStatus.OPEN.name());
        }

        if (report.getReportedAt() == null) {
            report.setReportedAt(LocalDateTime.now());
        }

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

    private RentalDamageReportStatus resolveStatus(String status) {
        try {
            return RentalDamageReportStatus.fromString(status);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("Invalid rental damage report status");
        }
    }

    private List<RentalDamageReportDTO> toDTOListWithLinks(List<RentalDamageReport> reports) {
        List<RentalDamageReportDTO> dtos = mapper.toDTOList(reports);
        dtos.forEach(this::addHateoasLinks);
        return dtos;
    }

    private void addHateoasLinks(RentalDamageReportDTO dto) {
        dto.add(linkTo(methodOn(RentalDamageReportController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(RentalDamageReportController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(RentalDamageReportController.class).findByStatus(dto.getStatus())).withRel("findByStatus").withType("GET"));
        dto.add(linkTo(methodOn(RentalDamageReportController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(RentalDamageReportController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(RentalDamageReportController.class).resolve(dto.getId())).withRel("resolve").withType("PUT"));
        dto.add(linkTo(methodOn(RentalDamageReportController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
