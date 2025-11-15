package com.remake.poki.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnlockRewardsDTO {
    private int coins;
    private int gems;
    private int experience;
    private String[] items;
}
