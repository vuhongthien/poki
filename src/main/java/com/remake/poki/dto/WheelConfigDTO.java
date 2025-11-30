package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WheelConfigDTO {
    private List<WheelPrizeDTO> prizes;
    private int spinCost;           // Gold cần để quay (10000)
    private int userGold;           // Gold hiện tại của user
    private int userWheel;          // Số lượt quay miễn phí còn lại
}
