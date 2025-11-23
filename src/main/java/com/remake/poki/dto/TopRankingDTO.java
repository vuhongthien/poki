package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopRankingDTO {
    private Long userId;
    private String userName;
    private Long currentPetId;
    private Long avtId;
    private int level;
    private int totalCombatPower; // Lực chiến = tổng attack + hp của tất cả pet
    private int rank; // Top 1, Top 2, ...
}
