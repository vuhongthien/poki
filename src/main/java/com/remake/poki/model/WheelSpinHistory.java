package com.remake.poki.model;

import com.remake.poki.enums.PrizeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wheel_spin_history")
public class WheelSpinHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    private PrizeType prizeType;
    
    private Long prizeId;
    
    private String prizeName;
    
    private int amount;
    
    private int goldSpent;
    
    private LocalDateTime spinTime;
    private boolean usedFreeTicket;

    private int slotIndex;          // Vị trí đã quay trúng
}
