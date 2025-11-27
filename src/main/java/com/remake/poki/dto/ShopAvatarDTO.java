package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopAvatarDTO {
    private Long id; // avatar.id (shop.itemId)
    public Long shopId;
    private String name;
    private int hp;
    private int attack;
    private int mana;
    private int price;
    private String currencyType;
    private boolean owned; // true nếu đã mua
}
