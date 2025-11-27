package com.remake.poki.repo;

import com.remake.poki.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    
    /**
     * Lấy tất cả items đang active trong shop
     */
    List<Shop> findByIsActiveTrueOrderBySortOrderAsc();
    
    /**
     * Lấy items theo loại
     */
    List<Shop> findByItemTypeAndIsActiveTrueOrderBySortOrderAsc(String itemType);
}
