package com.project.golfofficeapi.repository;

import com.project.golfofficeapi.model.ReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReceiptItemRepository extends JpaRepository<ReceiptItem, Long> {

    List<ReceiptItem> findByReceipt_PlayDate(LocalDate playDate);

    List<ReceiptItem> findByReceipt_Id(Long receiptId);

    default List<ReceiptItem> findByReceiptId(Long receiptId) {
        return findByReceipt_Id(receiptId);
    }

    void deleteByReceipt_Id(Long receiptId);

    default void deleteByReceiptId(Long receiptId) {
        deleteByReceipt_Id(receiptId);
    }
}
