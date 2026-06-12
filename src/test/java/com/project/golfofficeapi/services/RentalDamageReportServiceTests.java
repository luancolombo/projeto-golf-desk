package com.project.golfofficeapi.services;

import com.project.golfofficeapi.dto.RentalDamageReportDTO;
import com.project.golfofficeapi.enums.RentalDamageReportStatus;
import com.project.golfofficeapi.enums.RentalTransactionStatus;
import com.project.golfofficeapi.mapper.custom.RentalDamageReportMapper;
import com.project.golfofficeapi.model.RentalDamageReport;
import com.project.golfofficeapi.model.RentalItem;
import com.project.golfofficeapi.model.RentalTransaction;
import com.project.golfofficeapi.repository.RentalDamageReportRepository;
import com.project.golfofficeapi.repository.RentalItemRepository;
import com.project.golfofficeapi.repository.RentalTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RentalDamageReportServiceTests {

    @Mock
    private RentalDamageReportRepository repository;
    @Mock
    private RentalTransactionRepository rentalTransactionRepository;
    @Mock
    private RentalItemRepository rentalItemRepository;
    @Mock
    private CashRegisterClosureGuardService cashRegisterClosureGuardService;

    private RentalDamageReportService service;

    @BeforeEach
    void setUp() {
        service = new RentalDamageReportService(
                repository,
                rentalTransactionRepository,
                rentalItemRepository,
                new RentalDamageReportMapper(),
                cashRegisterClosureGuardService
        );
    }

    @Test
    void reportTransactionDamageMarksRentalAsDamagedAndCreatesOpenReport() {
        RentalItem rentalItem = rentalItem();
        RentalTransaction rentalTransaction = rentalTransaction(rentalItem, RentalTransactionStatus.RENTED);
        RentalDamageReportDTO request = new RentalDamageReportDTO();
        request.setDescription("Wheel damaged");
        request.setDamagedUnitLabel("Buggy #4");

        when(rentalTransactionRepository.findById(10L)).thenReturn(Optional.of(rentalTransaction));
        when(repository.existsByRentalTransaction_IdAndStatus(10L, RentalDamageReportStatus.OPEN)).thenReturn(false);
        when(repository.save(any(RentalDamageReport.class))).thenAnswer(invocation -> {
            RentalDamageReport report = invocation.getArgument(0);
            report.setId(99L);
            return report;
        });

        RentalDamageReportDTO result = service.reportTransactionDamage(10L, request);

        assertEquals(RentalTransactionStatus.DAMAGED, rentalTransaction.getStatus());
        assertEquals(99L, result.getId());
        assertEquals(10L, result.getRentalTransactionId());
        assertEquals(5L, result.getRentalItemId());
        assertEquals("Buggy #4", result.getDamagedUnitLabel());
        assertEquals(RentalDamageReportStatus.OPEN.name(), result.getStatus());
        verify(rentalTransactionRepository).save(rentalTransaction);
    }

    @Test
    void resolveReturnsDamagedRentalToStockWhenNoOtherOpenReportsExist() {
        RentalItem rentalItem = rentalItem();
        RentalTransaction rentalTransaction = rentalTransaction(rentalItem, RentalTransactionStatus.DAMAGED);
        RentalDamageReport report = new RentalDamageReport();
        report.setId(99L);
        report.setRentalTransaction(rentalTransaction);
        report.setRentalItem(rentalItem);
        report.setDescription("Wheel damaged");
        report.setStatus(RentalDamageReportStatus.OPEN);

        when(repository.findById(99L)).thenReturn(Optional.of(report));
        when(repository.save(any(RentalDamageReport.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.countByRentalTransaction_IdAndStatusAndIdNot(10L, RentalDamageReportStatus.OPEN, 99L)).thenReturn(0L);

        RentalDamageReportDTO result = service.resolve(99L);

        assertEquals(RentalDamageReportStatus.RESOLVED.name(), result.getStatus());
        assertEquals(RentalTransactionStatus.RETURNED, rentalTransaction.getStatus());
        assertEquals(9, rentalItem.getAvailableStock());
        verify(rentalItemRepository).save(rentalItem);
        verify(rentalTransactionRepository).save(rentalTransaction);
    }

    private RentalItem rentalItem() {
        RentalItem rentalItem = new RentalItem();
        rentalItem.setId(5L);
        rentalItem.setName("Buggy");
        rentalItem.setType("Material");
        rentalItem.setTotalStock(10);
        rentalItem.setAvailableStock(8);
        rentalItem.setRentalPrice(new BigDecimal("55.00"));
        rentalItem.setActive(true);
        return rentalItem;
    }

    private RentalTransaction rentalTransaction(RentalItem rentalItem, RentalTransactionStatus status) {
        RentalTransaction rentalTransaction = new RentalTransaction();
        rentalTransaction.setId(10L);
        rentalTransaction.setRentalItem(rentalItem);
        rentalTransaction.setQuantity(1);
        rentalTransaction.setStatus(status);
        rentalTransaction.setUnitPrice(new BigDecimal("55.00"));
        rentalTransaction.setTotalPrice(new BigDecimal("55.00"));
        return rentalTransaction;
    }
}
