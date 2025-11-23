package com.remake.poki.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BossRankingPlayerDTO {
    private Long userId;
    private String userName;
    private Long petId;
    private Long bossId;
    private int totalDamage;
    private int rank;
    private boolean canClaimReward;
    private boolean rewardClaimed;
}