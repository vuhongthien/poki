package com.remake.poki.repo;

import com.remake.poki.model.UserCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCardRepository extends JpaRepository<UserCard, Long> {
    boolean existsByUserIdAndCardId(Long id, Long itemId);

    @Query("SELECT uc FROM UserCard uc WHERE uc.userId = :userId")
    List<UserCard> findByUserId(@Param("userId") Long userId);
}
