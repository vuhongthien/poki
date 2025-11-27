package com.remake.poki.model;

import com.remake.poki.enums.ElementType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shop")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Shop {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "item_type", nullable = false)
    private String itemType; // "STONE", "ENERGY", "WHEEL", "STAR_WHITE", "STAR_BLUE", "STAR_RED", "CARD", "PET", "AVATAR"
    
    @Column(name = "item_name", nullable = false)
    private String itemName;
    
    @Column(name = "item_id")
    private Long itemId; // ID của stone/card/pet/avatar trong bảng tương ứng
    
    @Column(name = "price", nullable = false)
    private int price;
    
    @Column(name = "currency_type", nullable = false)
    private String currencyType; // "GOLD" hoặc "RUBY"
    
    @Column(name = "value")
    private int value; // Số lượng (energy = 20, wheel = 1, star = 10, etc)
    
    // Cho stone
    @Enumerated(EnumType.STRING)
    @Column(name = "element_type")
    private ElementType elementType;
    
    @Column(name = "level")
    private Integer level;
    
    // Giới hạn mua
    @Column(name = "max_purchase_per_day")
    private Integer maxPurchasePerDay; // null = unlimited, 3 = max 3 lần/ngày
    
    @Column(name = "is_active")
    private boolean isActive = true; // Bật/tắt item trong shop
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0; // Thứ tự hiển thị
    
    @Column(name = "description")
    private String description;
}
