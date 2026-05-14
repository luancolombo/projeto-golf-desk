package com.project.golfofficeapi.repository;

import com.project.golfofficeapi.model.CashRegisterClosureItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CashRegisterClosureItemRepository extends JpaRepository<CashRegisterClosureItem, Long> {

    List<CashRegisterClosureItem> findByCashRegisterClosure_Id(Long cashRegisterClosureId);

    default List<CashRegisterClosureItem> findByCashRegisterClosureId(Long cashRegisterClosureId) {
        return findByCashRegisterClosure_Id(cashRegisterClosureId);
    }

    void deleteByCashRegisterClosure_Id(Long cashRegisterClosureId);

    default void deleteByCashRegisterClosureId(Long cashRegisterClosureId) {
        deleteByCashRegisterClosure_Id(cashRegisterClosureId);
    }
}
