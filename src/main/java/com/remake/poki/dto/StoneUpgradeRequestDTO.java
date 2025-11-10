package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoneUpgradeRequestDTO {
    private Long userId;
    private Long[] stoneIds; // 3 stone IDs cùng level
    private boolean success; // Client đã random
}

