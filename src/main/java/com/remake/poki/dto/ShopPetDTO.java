package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopPetDTO {
    private Long shopId; // pet.id (shop.itemId)
    private Long id; // pet.id (shop.itemId)
    private String name;
    private int attack;
    private int hp;
    private int mana;
    private int price;
    private String currencyType;
    private String elementType;
    private int purchaseCount; // Đã mua chưa (0 hoặc 1)
    private int maxPurchasePerDay; // 1 = chỉ mua 1 lần
    private boolean canPurchase; // true nếu chưa mua
}
