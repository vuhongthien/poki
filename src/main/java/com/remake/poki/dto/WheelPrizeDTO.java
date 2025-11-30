package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WheelPrizeDTO {
    private int index;              // Vị trí trên vòng quay (0-11)
    private String prizeType;       // PET, AVATAR, GOLD, RUBY, ENERGY, STONE
    private Long prizeId;           // ID của prize (petId, avatarId, stoneId...)
    private String prizeName;       // Tên hiển thị
    private int amount;             // Số lượng
    private double chance;          // Tỉ lệ % (2 cho pet, 5 cho avatar...)
    private String rarity;          // LEGENDARY, EPIC, RARE, COMMON
    private String iconPath;        // Path để load sprite
    
    // Cho Stone
    private String elementType;     // FIRE, WATER, WOOD, EARTH, METAL
    private int stoneLevel;         // Level của đá (5 hoặc 6)
}
