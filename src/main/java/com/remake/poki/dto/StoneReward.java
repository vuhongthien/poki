package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoneReward {
    private Long stoneId;
    private Integer count;
    private String stoneName; // For display
    private String elementType; // FIRE, WATER, WIND, EARTH, THUNDER
    private Integer level; // 1-7
}