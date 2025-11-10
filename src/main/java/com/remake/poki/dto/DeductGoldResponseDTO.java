package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeductGoldResponseDTO {
    private boolean success;
    private String message;
    private int remainingGold;

    @Override
    public String toString() {
        return "DeductGoldResponseDTO{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", remainingGold=" + remainingGold +
                '}';
    }
}