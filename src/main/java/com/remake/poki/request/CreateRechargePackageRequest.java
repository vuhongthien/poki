package com.remake.poki.request;

import com.remake.poki.enums.PackageType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateRechargePackageRequest {
    private String name;
    private String description;
    private PackageType packageType;
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

    // Special rewards
    private Long petId;
    private Long cardId;
    private Long avtId;
    private String stonesJson;

    // Special flags
    private Boolean isFirstTimePurchase;
    private Boolean isLimitedQuantity;
    private Integer maxQuantity;
    private Integer bonusGoldPercent;

    // Time
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String iconUrl;
}