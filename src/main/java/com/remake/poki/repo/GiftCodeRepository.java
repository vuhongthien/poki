package com.remake.poki.repo;

import com.remake.poki.model.GiftCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface GiftCodeRepository extends JpaRepository<GiftCode, Long> {
    Optional<GiftCode> findByCode(String code);
}