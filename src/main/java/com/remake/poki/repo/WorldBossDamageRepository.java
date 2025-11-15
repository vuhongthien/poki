package com.remake.poki.repo;

import com.remake.poki.model.WorldBossDamage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorldBossDamageRepository extends JpaRepository<WorldBossDamage, Long> {

    Optional<WorldBossDamage> findByUserIdAndBossScheduleId(Long userId, Long bossScheduleId);

    List<WorldBossDamage> findTop10ByBossScheduleIdOrderByTotalDamageDesc(Long bossScheduleId);

    @Query("SELECT COUNT(d) FROM WorldBossDamage d WHERE d.bossScheduleId = ?1 AND d.totalDamage > ?2")
    long countPlayersWithHigherDamage(Long bossScheduleId, int damage);

    List<WorldBossDamage> findByBossScheduleId(Long bossScheduleId);
}