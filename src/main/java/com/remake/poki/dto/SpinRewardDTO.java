package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpinRewardDTO {
    private int spinIndex;          // Lần quay thứ mấy (0-9)
    private int prizeIndex;         // Index trên vòng quay (0-11)
    private String prizeType;
    private Long prizeId;
    private String prizeName;
    private int amount;
    private String rarity;
    private String iconPath;
    private String elementType;
    private int stoneLevel;
    public boolean isDuplicate;
    public int compensationGold;
}
