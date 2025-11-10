package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoneGroupDTO {
    private long stoneId;        // ID của đá
    private int quantity;        // Số lượng (luôn là 3)
    private boolean success;     // Kết quả random từ client

    @Override
    public String toString() {
        return "StoneGroup{" +
                "stoneId=" + stoneId +
                ", qty=" + quantity +
                ", success=" + success +
                '}';
    }
}