package com.remake.poki.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimRewardResponseDTO {
    private boolean success;
    private String message;
    private RewardDetailDTO reward;
}