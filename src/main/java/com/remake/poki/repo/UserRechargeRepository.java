package com.remake.poki.repo;

import com.remake.poki.model.UserRecharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRechargeRepository extends JpaRepository<UserRecharge, Long> {
    
    List<UserRecharge> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    Optional<UserRecharge> findByTransactionId(String transactionId);
    
    @Query("SELECT COALESCE(SUM(ur.amount), 0) FROM UserRecharge ur WHERE ur.userId = :userId AND ur.status = :status")
    Integer sumAmountByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    List<UserRecharge> findByStatus(String pending);
}
