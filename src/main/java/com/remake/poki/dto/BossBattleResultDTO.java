package com.remake.poki.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BossBattleResultDTO {
    private Long userId;
    private Long bossScheduleId;
    private int damageDealt;
    private boolean victory;
    private int turnCount;
}