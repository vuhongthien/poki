package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor; /**
 * DTO cho mỗi slot sao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StarSlotDTO {
    private Long slotId;
    private int starType; // 1=sao1, 2=sao2, 3=sao3
    private int slotPosition; // Vị trí trong ImageHT
    private int requiredStarCount; // Số sao cần
    private boolean inlaid; // Đã khảm chưa
    private boolean canInlay; // Có đủ sao để khảm không
}
