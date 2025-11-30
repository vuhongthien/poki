package com.remake.poki.repo;

import com.remake.poki.model.AuditReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRewardRepository extends JpaRepository<AuditReward, Long> {
    boolean existsByUserIdAndPetId(Long userId, Long petId);
}
