package com.project.golfofficeapi.repository;

import com.project.golfofficeapi.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    boolean existsByTeeTime_Id(Long teeTimeId);

    default boolean existsByTeeTimeId(Long teeTimeId) {
        return existsByTeeTime_Id(teeTimeId);
    }
}
