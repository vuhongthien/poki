package com.remake.poki.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_recharges")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRecharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "package_id")
    private Long packageId; // null nếu nạp tự do

    @Column(nullable = false)
    private Integer amount; // Số tiền nạp (VNĐ)

    @Column(nullable = false)
    private Integer goldReceived; // Số gold nhận được

    // Các reward khác
    private Integer rubyReceived;
    private Integer energyReceived;
    private Integer expReceived;
    private Integer starWhiteReceived;
    private Integer starBlueReceived;
    private Integer starRedReceived;
    private Integer wheelReceived;

    private Long petReceived;
    private Long cardReceived;

    @Column(name = "stones_received_json", columnDefinition = "TEXT")
    private String stonesReceivedJson;

    @Column(nullable = false)
    private String transactionId; // Mã giao dịch

    @Column(nullable = false)
    private String paymentMethod; // MOMO, ZALOPAY, BANKING, CARD...

    @Column(nullable = false)
    private String status; // PENDING, SUCCESS, FAILED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    private String note; // Ghi chú

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
