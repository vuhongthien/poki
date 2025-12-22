package com.remake.poki.model;

import com.remake.poki.enums.PackageStatus;
import com.remake.poki.enums.PackageType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recharge_packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargePackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PackageType packageType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PackageStatus status;

    @Column(nullable = false)
    private Integer price; // Giá VNĐ

    @Column(nullable = false)
    private Integer sortOrder; // Thứ tự hiển thị

    // === REWARDS ===
    @Column(nullable = false)
    private Integer gold;

    private Integer ruby;
    private Integer energy;
    private Integer exp;
    private Integer starWhite;
    private Integer starBlue;
    private Integer starRed;
    private Integer wheel;
    private Integer wheelDay;

    // Rewards đặc biệt
    private Long petId;
    private Long cardId;
    private Long avtId;

    // Multiple stones support
    @Column(name = "stones_json", columnDefinition = "TEXT")
    private String stonesJson; // [{"stoneId":1,"count":5},...]

    // === SPECIAL FLAGS ===
    @Column(nullable = false)
    private Boolean isFirstTimePurchase = false; // Chỉ mua 1 lần duy nhất

    @Column(nullable = false)
    private Boolean isLimitedQuantity = false; // Giới hạn số lượng

    private Integer maxQuantity; // Số lượng tối đa (null = unlimited)

    private Integer soldCount = 0; // Đã bán bao nhiêu

    // Thời gian hoạt động
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    // === BONUS ===
    private Integer bonusGoldPercent; // % gold thêm (vd: 100 = x2 gold)

    private String iconUrl; // URL icon gói hỗ trợ

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = PackageStatus.ACTIVE;
        }
        if (soldCount == null) {
            soldCount = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Kiểm tra gói hỗ trợ còn khả dụng không
     */
    public boolean isAvailable() {
        if (status != PackageStatus.ACTIVE) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (startTime != null && now.isBefore(startTime)) {
            return false;
        }
        if (endTime != null && now.isAfter(endTime)) {
            return false;
        }
        
        if (isLimitedQuantity && maxQuantity != null && soldCount >= maxQuantity) {
            return false;
        }
        
        return true;
    }

    /**
     * Tính tổng gold nhận được (bao gồm bonus)
     */
    public int calculateTotalGold() {
        if (bonusGoldPercent == null || bonusGoldPercent == 0) {
            return gold;
        }
        return gold + (gold * bonusGoldPercent / 100);
    }
}
