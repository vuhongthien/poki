package com.remake.poki.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "world_boss_schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorldBossSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long petId;
    private String bossName;
    private int bossLevel;
    private int bossHp;
    private int bossAttack;
    private int bossMana;
    private int startHour;
    private int startMinute;
    private int durationMinutes;
    private int rewardGold;
    private int rewardExp;
    private int rewardStarWhite;
    private int rewardStarBlue;
    private int rewardStarRed;
    private int displayOrder;
    private boolean isActive;
}