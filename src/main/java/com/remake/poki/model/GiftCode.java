package com.remake.poki.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "gift_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiftCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code; // "NEWYEAR2025"

    @Column(nullable = false)
    private String title; // "Quà Tết 2025"

    @Column(length = 500)
    private String description;

    // Rewards
    private Integer gold;
    private Integer energy;
    private Integer exp;
    private Integer starWhite;
    private Integer starBlue;
    private Integer starRed;
    private Integer wheel;

    private Long petId;
    private Long cardId;

    @Column(name = "stones_json", columnDefinition = "TEXT")
    private String stonesJson; // [{"element":"FIRE","level":1,"quantity":10}]

    @Column(name = "max_uses")
    private Integer maxUses; // null = unlimited

    @Column(name = "current_uses")
    private Integer currentUses = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (currentUses == null) currentUses = 0;
    }
}