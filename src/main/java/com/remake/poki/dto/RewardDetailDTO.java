package com.remake.poki.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardDetailDTO {
    private int rank;
    private int wheel;
    private int gold;
    private int energy;
}