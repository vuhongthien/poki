package com.remake.poki.repo;

import com.remake.poki.model.UserGiftCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGiftCodeRepository extends JpaRepository<UserGiftCode, Long> {
    boolean existsByUserIdAndGiftCodeId(Long userId, Long giftCodeId);
}