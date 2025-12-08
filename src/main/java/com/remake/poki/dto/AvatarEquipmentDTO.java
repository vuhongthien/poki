package com.remake.poki.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvatarEquipmentDTO {
    private Long id;
    private Long avatarId;
    private String name;
    private int hp;
    private int attack;
    private int mana;
    private String blind;  // Y/N
    private boolean isEquipped;  // true nếu đang được trang bị
}
