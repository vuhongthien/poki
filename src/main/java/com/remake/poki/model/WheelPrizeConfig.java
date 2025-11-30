package com.remake.poki.model;

import com.remake.poki.enums.ElementType;
import com.remake.poki.enums.PrizeType;
import com.remake.poki.enums.Rarity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wheel_prize_config")
public class WheelPrizeConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private int slotIndex;          // Vị trí trên vòng quay (0-11)
    
    @Enumerated(EnumType.STRING)
    private PrizeType prizeType;    // PET, AVATAR, GOLD, RUBY, ENERGY, STONE
    
    private Long prizeId;           // ID cụ thể (petId, avatarId, stoneId...)
    
    private String prizeName;       // Tên hiển thị
    
    private int amount;             // Số lượng
    
    private double chance;          // Tỉ lệ % (2 cho pet, 5 cho avatar...)
    
    @Enumerated(EnumType.STRING)
    private Rarity rarity;          // LEGENDARY, EPIC, RARE, COMMON
    
    private String iconPath;        // Path để load sprite
    
    // Cho Stone
    @Enumerated(EnumType.STRING)
    private ElementType elementType; // FIRE, WATER, WOOD, EARTH, METAL
    
    private int stoneLevel;         // Level của đá (5 hoặc 6)
    
    private boolean active = true;  // Có đang active không
}
