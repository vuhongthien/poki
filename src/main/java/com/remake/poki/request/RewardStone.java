package com.remake.poki.request;

import lombok.Data;

@Data
public class RewardStone {
    private Integer level;      // 1-7
    private String element;     // Fire, Water, Earth, Wood, Metal
    private Integer quantity;
}
