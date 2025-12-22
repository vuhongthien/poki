package com.remake.poki.repo;

import com.remake.poki.model.UserPackagePurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPackagePurchaseRepository extends JpaRepository<UserPackagePurchase, Long> {
    
    Optional<UserPackagePurchase> findByUserIdAndPackageId(Long userId, Long packageId);
    
    boolean existsByUserIdAndPackageId(Long userId, Long packageId);
}
