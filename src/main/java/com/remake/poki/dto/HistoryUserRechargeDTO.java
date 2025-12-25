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
public class HistoryUserRechargeDTO {
    private Long userId;
    private Long packageId;
    private String packageName;
    private Integer amount;
    private LocalDateTime createTime;
}
