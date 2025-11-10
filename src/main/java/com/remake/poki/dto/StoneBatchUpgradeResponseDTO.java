package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoneBatchUpgradeResponseDTO {
    private boolean success;
    private String message;
    private int successCount;    // Số lần nâng cấp thành công
    private int failCount;       // Số lần thất bại
    private List<String> details; // Chi tiết từng lần upgrade

    @Override
    public String toString() {
        return "StoneBatchUpgradeResponseDTO{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", successCount=" + successCount +
                ", failCount=" + failCount +
                '}';
    }
}