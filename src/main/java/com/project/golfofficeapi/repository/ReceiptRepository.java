package com.project.golfofficeapi.repository;

import com.project.golfofficeapi.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    List<Receipt> findByBookingId(Long bookingId);

    List<Receipt> findByBookingPlayerId(Long bookingPlayerId);

    List<Receipt> findByPaymentId(Long paymentId);

    List<Receipt> findByReceiptNumberStartingWith(String prefix);

    Optional<Receipt> findFirstByPaymentIdAndCancelledFalse(Long paymentId);

    boolean existsByReceiptNumber(String receiptNumber);
}
