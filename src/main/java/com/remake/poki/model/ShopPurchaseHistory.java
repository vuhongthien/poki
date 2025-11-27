package com.remake.poki.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "shop_purchase_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopPurchaseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "shop_id", nullable = false)
    private Long shopId; // ID trong bảng shop

    @Column(name = "item_type", nullable = false)
    private String itemType;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "price_paid", nullable = false)
    private int pricePaid;

    @Column(name = "currency_type", nullable = false)
    private String currencyType;

    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;
}
