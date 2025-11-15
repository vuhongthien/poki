package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO cho thông tin Pet Huyền Thoại
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LegendPetDTO {
    private Long petId;
    private String name;
    private String description;
    private int totalStars; // Tổng số sao cần khảm
    private int inlaidStars; // Số sao đã khảm
    private boolean unlocked; // Đã mở khóa pet chưa
    private List<ImageHTDTO> images; // Danh sách 5 ImageHT
}

