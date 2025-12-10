package com.remake.poki.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "world_boss_damage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorldBossDamage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long userPetId;

    @Column(nullable = false)
    private Long bossScheduleId;

    @Column(nullable = false)
    private int totalDamage = 0;

    private int battleCount = 0;

    private LocalDateTime lastBattleTime;

    @Column(nullable = false)
    private boolean rewardClaimed = false;

    private LocalDateTime rewardClaimedTime;
}