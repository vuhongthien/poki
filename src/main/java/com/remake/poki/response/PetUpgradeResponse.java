package com.remake.poki.response;

import com.remake.poki.dto.UserPetDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetUpgradeResponse {
    private boolean success;
    private String message;
    private UserPetDTO updatedPet;
}
