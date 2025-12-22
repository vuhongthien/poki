package com.remake.poki.model;

import com.remake.poki.enums.GiftStatus;
import com.remake.poki.enums.GiftType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "gifts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId; // null = tất cả user

    @Column(nullable = false)
    private String title; // "Quà Tết 2025"

    @Column(length = 500)
    private String description; // "Chúc mừng năm mới"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GiftType giftType; // INDIVIDUAL, ALL_USERS

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GiftStatus status; // PENDING, CLAIMED, EXPIRED

    // Rewards
    private Integer gold;
    private Integer energy;
    private Integer exp;
    private Integer starWhite;
    private Integer starBlue;
    private Integer starRed;
    private Integer wheel;
    private Integer wheelDay;
    private Integer ruby;

    // Foreign keys cho rewards phức tạp
    private Long petId;
    private Long avtId;
    private Long cardId;

    // Multiple stones support
    @Column(name = "stones_json", columnDefinition = "TEXT")
    private String stonesJson; // JSON format: [{"stoneId":1,"count":10},{"stoneId":5,"count":20}]

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "claimed_at")
    private LocalDateTime claimedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = GiftStatus.PENDING;
        }
    }
}