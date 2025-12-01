package com.remake.poki.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_gift_codes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "gift_code_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGiftCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "gift_code_id", nullable = false)
    private Long giftCodeId;

    @Column(name = "claimed_at")
    private LocalDateTime claimedAt;

    @PrePersist
    protected void onCreate() {
        claimedAt = LocalDateTime.now();
    }
}