package com.remake.poki.dto;

import com.remake.poki.enums.PackageStatus;
import com.remake.poki.enums.PackageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RechargePackageDTO {
    private Long id;
    private String name;
    private String description;
    private PackageType packageType;
    private PackageStatus status;
    private Integer price;
    private Integer sortOrder;
    
    // Rewards
    private Integer gold;
    private Integer ruby;
    private Integer energy;
    private Integer exp;
    private Integer starWhite;
    private Integer starBlue;
    private Integer starRed;
    private Integer wheel;
    private Integer wheelDay;
    private Long petId;
    private Long cardId;
    private Long avtId;
    private List<StoneRewardDTO> stones;
    
    // Special flags
    private Boolean isFirstTimePurchase;
    private Boolean isLimitedQuantity;
    private Integer maxQuantity;
    private Integer soldCount;
    private Integer remainingQuantity;
    
    // Time
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    // Bonus
    private Integer bonusGoldPercent;
    private Integer totalGold; // Gold sau khi tính bonus
    
    private String iconUrl;
    private Boolean isAvailable;
    private Boolean canPurchase; // User có thể mua không (check first time)
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
