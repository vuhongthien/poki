package com.remake.poki.repo;

import com.remake.poki.model.UserRecharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRechargeRepository extends JpaRepository<UserRecharge, Long> {

    List<UserRecharge> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<UserRecharge> findByTransactionId(String transactionId);

    @Query("SELECT COALESCE(SUM(ur.amount), 0) FROM UserRecharge ur WHERE ur.userId = :userId AND ur.status = :status")
    Integer sumAmountByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    List<UserRecharge> findByStatusOrderByCreatedAtDesc(String pending);

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM UserRecharge r WHERE r.status = :status")
    long totalAmountByStatus(@Param("status") String status);

    @Query("SELECT r FROM UserRecharge r WHERE r.status = :status AND r.userId = :userId ORDER BY r.createdAt DESC")
    List<UserRecharge> listAmountUserByStatus(@Param("status") String status, @Param("userId") Long userId);

    // ========== METHODS MỚI CHO PHÂN TRANG VÀ TÌM KIẾM THEO STATUS ==========

    /**
     * Lấy danh sách transactions theo status với phân trang
     * Sắp xếp: PENDING theo created_at DESC, SUCCESS theo completed_at DESC
     */
    @Query(value = "SELECT * FROM user_recharges WHERE status = :status " +
            "ORDER BY CASE WHEN :status = 'PENDING' THEN created_at ELSE completed_at END DESC " +
            "LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<UserRecharge> findTransactionsByStatusWithPagination(
            @Param("status") String status,
            @Param("limit") int limit,
            @Param("offset") int offset);

    /**
     * Đếm tổng số transactions theo status
     */
    @Query("SELECT COUNT(r) FROM UserRecharge r WHERE r.status = :status")
    long countTransactionsByStatus(@Param("status") String status);

    /**
     * Lấy danh sách transactions theo status và userId với phân trang
     */
    @Query(value = "SELECT * FROM user_recharges WHERE status = :status AND user_id = :userId " +
            "ORDER BY CASE WHEN :status = 'PENDING' THEN created_at ELSE completed_at END DESC " +
            "LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<UserRecharge> findTransactionsByStatusAndUserIdWithPagination(
            @Param("status") String status,
            @Param("userId") Long userId,
            @Param("limit") int limit,
            @Param("offset") int offset);

    /**
     * Đếm tổng số transactions theo status và userId
     */
    @Query("SELECT COUNT(r) FROM UserRecharge r WHERE r.status = :status AND r.userId = :userId")
    long countTransactionsByStatusAndUserId(@Param("status") String status, @Param("userId") Long userId);

    // ========== BACKWARD COMPATIBLE METHODS (giữ lại cho code cũ) ==========

    /**
     * Lấy danh sách SUCCESS transactions với phân trang
     * @deprecated Sử dụng findTransactionsByStatusWithPagination("SUCCESS", limit, offset) thay thế
     */
    @Deprecated
    @Query(value = "SELECT * FROM user_recharges WHERE status = 'SUCCESS' " +
            "ORDER BY completed_at DESC LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<UserRecharge> findSuccessTransactionsWithPagination(@Param("limit") int limit, @Param("offset") int offset);

    /**
     * Đếm tổng số SUCCESS transactions
     * @deprecated Sử dụng countTransactionsByStatus("SUCCESS") thay thế
     */
    @Deprecated
    @Query("SELECT COUNT(r) FROM UserRecharge r WHERE r.status = 'SUCCESS'")
    long countSuccessTransactions();

    /**
     * Lấy danh sách SUCCESS transactions của một user cụ thể với phân trang
     * @deprecated Sử dụng findTransactionsByStatusAndUserIdWithPagination("SUCCESS", userId, limit, offset) thay thế
     */
    @Deprecated
    @Query(value = "SELECT * FROM user_recharges WHERE status = 'SUCCESS' AND user_id = :userId " +
            "ORDER BY completed_at DESC LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<UserRecharge> findSuccessTransactionsByUserIdWithPagination(
            @Param("userId") Long userId,
            @Param("limit") int limit,
            @Param("offset") int offset);

    /**
     * Đếm tổng số SUCCESS transactions của một user
     * @deprecated Sử dụng countTransactionsByStatusAndUserId("SUCCESS", userId) thay thế
     */
    @Deprecated
    @Query("SELECT COUNT(r) FROM UserRecharge r WHERE r.status = 'SUCCESS' AND r.userId = :userId")
    long countSuccessTransactionsByUserId(@Param("userId") Long userId);
}