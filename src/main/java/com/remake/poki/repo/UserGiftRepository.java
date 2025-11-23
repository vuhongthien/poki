package com.remake.poki.repo;

import com.remake.poki.enums.GiftStatus;
import com.remake.poki.model.UserGift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGiftRepository extends JpaRepository<UserGift, Long> {

    /**
     * Tìm UserGift cụ thể
     */
    Optional<UserGift> findByUserIdAndGiftId(Long userId, Long giftId);

    /**
     * Kiểm tra user đã claim gift chưa
     */
    boolean existsByUserIdAndGiftIdAndStatus(Long userId, Long giftId, GiftStatus status);

    /**
     * Tìm tất cả gifts PENDING của user
     */
    @Query("SELECT ug FROM UserGift ug WHERE ug.userId = :userId AND ug.status = 'PENDING'")
    List<UserGift> findPendingGiftsByUserId(@Param("userId") Long userId);

    /**
     * Đếm số quà chưa nhận
     */
    long countByUserIdAndStatus(Long userId, GiftStatus status);

    /**
     * Lịch sử đã nhận
     */
    List<UserGift> findByUserIdAndStatusOrderByClaimedAtDesc(Long userId, GiftStatus status);
}