package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor; /**
 * Response sau khi khảm sao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InlayStarResponse {
    private boolean success;
    private String message;
    private int remainingWhiteStars;
    private int remainingBlueStars;
    private int remainingRedStars;
    private boolean petUnlocked; // Pet có được mở khóa sau khi khảm không
}
