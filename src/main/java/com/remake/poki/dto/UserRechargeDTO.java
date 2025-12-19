package com.remake.poki.dto;

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
public class UserRechargeDTO {
    private Long id;
    private Long userId;
    private Long packageId;
    private String packageName;
    private Integer amount;
    private Integer goldReceived;
    private Integer rubyReceived;
    private Integer energyReceived;
    private Integer expReceived;
    private Integer starWhiteReceived;
    private Integer starBlueReceived;
    private Integer starRedReceived;
    private Integer wheelReceived;
    private Long petReceived;
    private Long cardReceived;
    private List<StoneRewardDTO> stonesReceived;
    private String transactionId;
    private String paymentMethod;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String note;
}
