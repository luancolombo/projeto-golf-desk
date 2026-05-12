package com.project.golfofficeapi.repository;

import com.project.golfofficeapi.model.RentalItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalItemRepository extends JpaRepository<RentalItem, Long> {
}
