package com.remake.poki.repo;

import com.remake.poki.model.WorldBossSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface WorldBossScheduleRepository extends JpaRepository<WorldBossSchedule, Long> {

    // Sort theo startHour, sau đó startMinute
    List<WorldBossSchedule> findAllByIsActiveTrueOrderByStartHourAscStartMinuteAsc();

    // Hoặc dùng @Query nếu cần custom hơn
    @Query("SELECT w FROM WorldBossSchedule w WHERE w.isActive = true ORDER BY w.startHour ASC, w.startMinute ASC")
    List<WorldBossSchedule> findAllActiveOrderByStartTime();

    WorldBossSchedule findByPetId(Long petId);

    List<WorldBossSchedule> findByIsActiveTrueOrderByDisplayOrderAsc();

    @Query("SELECT w FROM WorldBossSchedule w WHERE w.isActive = true ORDER BY w.displayOrder ASC")
    List<WorldBossSchedule> findAllActiveBosses();
}