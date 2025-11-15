package com.remake.poki.repo;

import com.remake.poki.model.UserPetStar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPetStarRepository extends JpaRepository<UserPetStar, Long> {
    List<UserPetStar> findByUserIdAndPetId(Long userId, Long petId);
    List<UserPetStar> findByUserIdAndPetIdAndImageIndex(Long userId, Long petId, int imageIndex);
    Optional<UserPetStar> findByUserIdAndSlotId(Long userId, Long slotId);
    long countByUserIdAndPetIdAndInlaidTrue(Long userId, Long petId);
    long countByPetId(Long petId);
}
