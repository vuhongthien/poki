package com.remake.poki.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model định nghĩa các ô sao (slot) cho mỗi ImageHT của Pet Huyền Thoại
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pet_star_slots")
public class PetStarSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID của pet huyền thoại
    private Long petId;

    // Số thứ tự ImageHT (1-5)
    private int imageIndex;

    // Loại sao (1=sao1, 2=sao2, 3=sao3)
    private int starType;

    // Vị trí của sao trong ImageHT (1-5 hoặc nhiều hơn)
    private int slotPosition;

    // Số sao cần để khảm vào slot này
    private int requiredStarCount;
}