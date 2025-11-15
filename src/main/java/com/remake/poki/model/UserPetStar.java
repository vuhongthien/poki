package com.remake.poki.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Lưu trạng thái khảm sao của user cho từng slot
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_pet_stars")
public class UserPetStar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long petId;

    // Số thứ tự ImageHT
    private int imageIndex;

    // ID của slot sao
    private Long slotId;

    // Trạng thái: true = đã khảm, false = chưa khảm
    private boolean inlaid;

    // Thời gian khảm
    private LocalDateTime inlaidAt;
}