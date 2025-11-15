package com.remake.poki.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorldBossDTO {
    private Long id;
    private Long petId;
    private String bossName;
    private int bossLevel;
    private int bossHp;
    private int bossAttack;
    private int bossMana;
    private String elementType;
    private String startTime;
    private String endTime;
    private String status; // UPCOMING, ACTIVE, ENDED
    private int remainingAttempts;
    private int maxAttempts;
    private int currentDamage;
    private int userRank;
}