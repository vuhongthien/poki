package com.remake.poki.repo;

import com.remake.poki.model.UserAvatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAvatarRepository extends JpaRepository<UserAvatar, Long> {
    boolean existsByUserIdAndAvatarId(Long userId, Long itemId);
    List<UserAvatar> findByUserId(Long userId);

    Optional<UserAvatar> findByUserIdAndAvatarId(Long userId, Long avatarId);
}
