package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnergyInfoDTO {
    private int currentEnergy;
    private int maxEnergy;
    private long secondsUntilNextRegen; // Số giây đến lần hồi tiếp theo
    private String lastUpdateTime; // ISO format
}