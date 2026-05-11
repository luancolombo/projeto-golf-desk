package com.project.golfofficeapi.repository;

import com.project.golfofficeapi.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByFullNameContainingIgnoreCase(String fullName);

    boolean existsByTaxNumber(String taxNumber);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByTaxNumberAndIdNot(String taxNumber, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByPhoneAndIdNot(String phone, Long id);
}
