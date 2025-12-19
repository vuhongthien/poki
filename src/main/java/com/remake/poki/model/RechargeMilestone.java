package com.remake.poki.model;

import com.remake.poki.enums.GiftStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recharge_milestones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeMilestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // "Mốc 50K", "Mốc 5 Triệu"

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Integer requiredAmount; // Số tiền cần nạp (VNĐ)

    @Column(nullable = false)
    private Integer sortOrder; // Thứ tự hiển thị

    // === REWARDS ===
    private Integer gold;
    private Integer ruby;
    private Integer energy;
    private Integer exp;
    private Integer starWhite;
    private Integer starBlue;
    private Integer starRed;
    private Integer wheel;

    // Rewards đặc biệt
    private Long petId;
    private Long cardId;

    // Multiple stones support
    @Column(name = "stones_json", columnDefinition = "TEXT")
    private String stonesJson;

    private String iconUrl;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
