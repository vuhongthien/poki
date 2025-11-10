package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeductGoldRequestDTO {
    private int userId;
    private int amount;
    private String reason; // e.g., "upgrade_all_stones", "upgrade_pet_protection"

    @Override
    public String toString() {
        return "DeductGoldRequestDTO{" +
                "userId=" + userId +
                ", amount=" + amount +
                ", reason='" + reason + '\'' +
                '}';
    }
}