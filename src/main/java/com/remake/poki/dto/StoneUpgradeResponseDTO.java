package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoneUpgradeResponseDTO {
    private boolean success;
    private String message;
    private Integer newStoneLevel; // Level của đá sau khi nâng cấp
}