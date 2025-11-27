package com.remake.poki.repo;

import com.remake.poki.model.ShopPurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ShopPurchaseHistoryRepository extends JpaRepository<ShopPurchaseHistory, Long> {

    /**
     * Đếm số lần user đã mua item này sau một thời điểm
     * Dùng để check giới hạn mua trong ngày
     */
    int countByUserIdAndShopIdAndPurchaseDateAfter(Long userId, Long shopId, LocalDateTime after);

    /**
     * Check xem user đã mua item loại này chưa (cho pet/avatar - chỉ mua 1 lần)
     */
    boolean existsByUserIdAndItemTypeAndItemId(Long userId, String itemType, Long itemId);
}
