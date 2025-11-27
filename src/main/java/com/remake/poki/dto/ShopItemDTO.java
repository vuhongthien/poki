package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// =====================================================
// SHOP DTOs
// =====================================================

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopItemDTO {
    private int id; // shop.id
    private String itemType;
    private String name;
    private int price;
    private String currencyType;
    private int value;
    private String elementType; // Cho stone
    private int level; // Cho stone
    private Long cardId; // Cho card (shop.itemId)
    private boolean owned; // Luôn false cho items vì mua được nhiều lần
}

