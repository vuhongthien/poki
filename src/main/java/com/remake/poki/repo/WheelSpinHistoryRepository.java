package com.remake.poki.repo;

import com.remake.poki.model.WheelSpinHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WheelSpinHistoryRepository extends JpaRepository<WheelSpinHistory, Long> {
    
    List<WheelSpinHistory> findByUserIdOrderBySpinTimeDesc(Long userId);
    
    List<WheelSpinHistory> findByUserIdAndSpinTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
    
    long countByUserIdAndSpinTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
