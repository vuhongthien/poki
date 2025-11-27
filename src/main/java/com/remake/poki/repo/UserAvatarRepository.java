package com.remake.poki.repo;

import com.remake.poki.model.UserAvatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAvatarRepository extends JpaRepository<UserAvatar, Long> {
    boolean existsByUserIdAndAvatarId(Long userId, Long itemId);
}
