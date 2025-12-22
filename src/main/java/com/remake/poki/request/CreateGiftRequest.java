package com.remake.poki.request;

import com.remake.poki.dto.StoneReward;
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
public class CreateGiftRequest {

    private Long userId; // null nếu gửi cho tất cả
    private String title;
    private String description;
    private GiftType giftType;

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

    private Long petId;
    private Long avtId;
    private Long cardId;

    // Multiple stones - JSON format hoặc List
    private List<StoneReward> stones; // [{"stoneId": 1, "count": 10}, {"stoneId": 5, "count": 20}]

    private LocalDateTime expiredAt; // Thời gian hết hạn
}