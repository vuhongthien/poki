package com.remake.poki.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BossRankingDTO {
    private Long userId;
    private String userName;
    private int totalDamage;
    private int rank;
    private boolean isCurrentUser;
}