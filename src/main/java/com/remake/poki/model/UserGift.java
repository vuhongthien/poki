package com.remake.poki.model;

import com.remake.poki.enums.GiftStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Bảng trung gian: Tracking việc user đã nhận gift nào chưa
 * - Gift cho ALL_USERS: Mỗi user có 1 record riêng
 * - Gift INDIVIDUAL: Chỉ có 1 record
 */
@Entity
@Table(name = "user_gifts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "gift_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "gift_id", nullable = false)
    private Long giftId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GiftStatus status; // PENDING, CLAIMED, EXPIRED

    @Column(name = "claimed_at")
    private LocalDateTime claimedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = GiftStatus.PENDING;
        }
    }
}