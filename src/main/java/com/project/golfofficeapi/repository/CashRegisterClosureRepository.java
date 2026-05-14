package com.project.golfofficeapi.repository;

import com.project.golfofficeapi.enums.CashRegisterClosureStatus;
import com.project.golfofficeapi.model.CashRegisterClosure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CashRegisterClosureRepository extends JpaRepository<CashRegisterClosure, Long> {

    Optional<CashRegisterClosure> findByBusinessDate(LocalDate businessDate);

    List<CashRegisterClosure> findByStatus(CashRegisterClosureStatus status);

    boolean existsByBusinessDateAndStatus(LocalDate businessDate, CashRegisterClosureStatus status);
}
