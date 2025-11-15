package com.remake.poki.response;

import com.remake.poki.dto.PetDataDTO;
import com.remake.poki.dto.UnlockRewardsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnlockPetResponse {
    private boolean success;
    private String message;
    private PetDataDTO petData;
    private UnlockRewardsDTO rewards;
}