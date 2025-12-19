package com.remake.poki.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_package_purchases",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "package_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPackagePurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "package_id", nullable = false)
    private Long packageId;

    @Column(nullable = false)
    private Integer purchaseCount = 1;

    @Column(name = "first_purchase_at", nullable = false)
    private LocalDateTime firstPurchaseAt;

    @Column(name = "last_purchase_at")
    private LocalDateTime lastPurchaseAt;

    @PrePersist
    protected void onCreate() {
        if (firstPurchaseAt == null) {
            firstPurchaseAt = LocalDateTime.now();
        }
        lastPurchaseAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastPurchaseAt = LocalDateTime.now();
    }
}
