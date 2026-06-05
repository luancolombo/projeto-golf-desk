package com.project.golfofficeapi.repository;

import com.project.golfofficeapi.enums.RentalDamageReportStatus;
import com.project.golfofficeapi.model.RentalDamageReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalDamageReportRepository extends JpaRepository<RentalDamageReport, Long> {

    List<RentalDamageReport> findByStatus(RentalDamageReportStatus status);

    Page<RentalDamageReport> findByStatus(RentalDamageReportStatus status, Pageable pageable);

    List<RentalDamageReport> findByRentalItem_Id(Long rentalItemId);

    Page<RentalDamageReport> findByRentalItem_Id(Long rentalItemId, Pageable pageable);

    default List<RentalDamageReport> findByRentalItemId(Long rentalItemId) {
        return findByRentalItem_Id(rentalItemId);
    }

    List<RentalDamageReport> findByRentalTransaction_Id(Long rentalTransactionId);

    Page<RentalDamageReport> findByRentalTransaction_Id(Long rentalTransactionId, Pageable pageable);

    default List<RentalDamageReport> findByRentalTransactionId(Long rentalTransactionId) {
        return findByRentalTransaction_Id(rentalTransactionId);
    }
}
