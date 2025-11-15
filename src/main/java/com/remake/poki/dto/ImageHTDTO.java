package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List; /**
 * DTO cho mỗi ImageHT
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageHTDTO {
    private int imageIndex; // 1-5
    private List<StarSlotDTO> starSlots; // Danh sách các slot sao
}
