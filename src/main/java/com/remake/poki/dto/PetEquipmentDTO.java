package com.remake.poki.dto;

import com.remake.poki.enums.ElementType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetEquipmentDTO {
    private Long id;
    private Long petId;
    private String name;
    private int level;
    private int hp;
    private int attack;
    private int mana;
    private ElementType elementType;
    private boolean isEquipped;  // true nếu đang được trang bị
}
