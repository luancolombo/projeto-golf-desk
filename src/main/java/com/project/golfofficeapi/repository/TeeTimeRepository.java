package com.project.golfofficeapi.repository;

import com.project.golfofficeapi.model.TeeTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface TeeTimeRepository extends JpaRepository<TeeTime, Long> {

    boolean existsByPlayDateAndStartTime(LocalDate playDate, LocalTime startTime);

    boolean existsByPlayDateAndStartTimeAndIdNot(LocalDate playDate, LocalTime startTime, Long id);
}
