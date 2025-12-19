package com.remake.poki.dto;

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
public class RechargeMilestoneDTO {
    private Long id;
    private String name;
    private String description;
    private Integer requiredAmount;
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
    private Long petId;
    private Long cardId;
    private List<StoneRewardDTO> stones;
    
    private String iconUrl;
    private Boolean isActive;
    
    // User progress
    private Integer userTotalRecharge; // Tổng nạp của user
    private Boolean canClaim; // Đủ điều kiện nhận chưa
    private Boolean claimed; // Đã nhận chưa
    private LocalDateTime claimedAt;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
