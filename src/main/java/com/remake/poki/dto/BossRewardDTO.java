package com.remake.poki.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BossRewardDTO {
    private int gold;
    private int exp;
    private int starWhite;
    private int starBlue;
    private int starRed;
    private int rank;
    private int totalDamage;
}