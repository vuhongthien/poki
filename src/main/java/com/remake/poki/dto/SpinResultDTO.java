package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpinResultDTO {
    private List<SpinRewardDTO> rewards;
    private int totalGoldSpent;
    private int remainingGold;
    private int remainingWheel;
    private boolean success;
    private String message;
    public boolean isDuplicate;
    public int compensationGold;
}
