package com.remake.poki.dto;

import com.remake.poki.enums.GiftStatus;
import com.remake.poki.enums.GiftType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiftDTO {

    private Long id;
    private Long userId;
    private String title;
    private String description;
    private GiftType giftType;
    private GiftStatus status;

    // Rewards
    private Integer gold;
    private Integer energy;
    private Integer exp;
    private Integer starWhite;
    private Integer starBlue;
    private Integer starRed;
    private Integer wheel;
    private Integer wheelDay;
    private Integer ruby;

    // Complex rewards
    private Long petId;
    private Long avtId;
    private String petName;

    private Long cardId;
    private String cardName;

    // Multiple stones support
    private List<StoneReward> stones;

    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private LocalDateTime claimedAt;

    // Helper để hiển thị
    public String getRewardSummary() {
        StringBuilder sb = new StringBuilder();

        if (gold != null && gold > 0) sb.append("Vàng: ").append(gold).append(", ");
        if (energy != null && energy > 0) sb.append("Năng lượng: ").append(energy).append(", ");
        if (exp != null && exp > 0) sb.append("EXP: ").append(exp).append(", ");
        if (starWhite != null && starWhite > 0) sb.append("Sao trắng: ").append(starWhite).append(", ");
        if (starBlue != null && starBlue > 0) sb.append("Sao xanh: ").append(starBlue).append(", ");
        if (starRed != null && starRed > 0) sb.append("Sao đỏ: ").append(starRed).append(", ");
        if (wheel != null && wheel > 0) sb.append("Vòng quay: ").append(wheel).append(", ");
        if (petName != null) sb.append("Pet: ").append(petName).append(", ");
        if (cardName != null) sb.append("Card: ").append(cardName).append(", ");

        // Multiple stones
        if (stones != null && !stones.isEmpty()) {
            sb.append("Đá: ");
            for (StoneReward stone : stones) {
                sb.append(stone.getStoneName()).append(" x").append(stone.getCount()).append(", ");
            }
        }

        String result = sb.toString();
        return result.isEmpty() ? "Không có quà" : result.substring(0, result.length() - 2);
    }
}