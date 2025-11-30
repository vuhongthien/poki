package com.remake.poki.repo;

import com.remake.poki.model.WheelPrizeConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WheelPrizeConfigRepository extends JpaRepository<WheelPrizeConfig, Long> {
    
    List<WheelPrizeConfig> findByActiveOrderBySlotIndexAsc(boolean active);
    
    List<WheelPrizeConfig> findAllByOrderBySlotIndexAsc();
}
