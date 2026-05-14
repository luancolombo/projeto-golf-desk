package com.project.golfofficeapi.repository;

import com.project.golfofficeapi.enums.RentalDamageReportStatus;
import com.project.golfofficeapi.model.RentalDamageReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalDamageReportRepository extends JpaRepository<RentalDamageReport, Long> {

    List<RentalDamageReport> findByStatus(RentalDamageReportStatus status);

    List<RentalDamageReport> findByRentalItem_Id(Long rentalItemId);

    default List<RentalDamageReport> findByRentalItemId(Long rentalItemId) {
        return findByRentalItem_Id(rentalItemId);
    }

    List<RentalDamageReport> findByRentalTransaction_Id(Long rentalTransactionId);

    default List<RentalDamageReport> findByRentalTransactionId(Long rentalTransactionId) {
        return findByRentalTransaction_Id(rentalTransactionId);
    }
}
