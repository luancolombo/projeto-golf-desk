package com.project.golfofficeapi.repository;

import com.project.golfofficeapi.model.ReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptItemRepository extends JpaRepository<ReceiptItem, Long> {

    List<ReceiptItem> findByReceiptId(Long receiptId);

    void deleteByReceiptId(Long receiptId);
}
