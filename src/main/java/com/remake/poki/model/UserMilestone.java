package com.remake.poki.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_milestones", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "milestone_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMilestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "milestone_id", nullable = false)
    private Long milestoneId;

    @Column(nullable = false)
    private Boolean claimed = false; // Đã nhận quà chưa

    @Column(name = "claimed_at")
    private LocalDateTime claimedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
