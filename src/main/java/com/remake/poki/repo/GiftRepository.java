package com.remake.poki.repo;

import com.remake.poki.enums.GiftStatus;
import com.remake.poki.model.Gift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GiftRepository extends JpaRepository<Gift, Long> {
    
    /**
     * Tìm tất cả quà PENDING của user
     * Bao gồm: quà cá nhân + quà cho tất cả user
     */
    @Query("SELECT g FROM Gift g WHERE g.status = 'PENDING' " +
           "AND (g.userId = :userId OR g.userId IS NULL) " +
           "AND (g.expiredAt IS NULL OR g.expiredAt > :now) " +
           "ORDER BY g.createdAt DESC")
    List<Gift> findPendingGiftsByUserId(@Param("userId") Long userId, 
                                        @Param("now") LocalDateTime now);
    
    /**
     * Đếm số quà chưa nhận
     */
    @Query("SELECT COUNT(g) FROM Gift g WHERE g.status = 'PENDING' " +
           "AND (g.userId = :userId OR g.userId IS NULL) " +
           "AND (g.expiredAt IS NULL OR g.expiredAt > :now)")
    long countPendingGifts(@Param("userId") Long userId, 
                          @Param("now") LocalDateTime now);
    
    /**
     * Tìm quà đã hết hạn cần update status
     */
    @Query("SELECT g FROM Gift g WHERE g.status = 'PENDING' " +
           "AND g.expiredAt IS NOT NULL AND g.expiredAt <= :now")
    List<Gift> findExpiredGifts(@Param("now") LocalDateTime now);
    
    /**
     * Tìm lịch sử quà đã nhận
     */
    List<Gift> findByUserIdAndStatusOrderByClaimedAtDesc(Long userId, GiftStatus status);
}
