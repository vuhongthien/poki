package com.remake.poki.repo;

import com.remake.poki.model.PetStarSlot;
import com.remake.poki.model.UserPetStar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetStarSlotRepository extends JpaRepository<PetStarSlot, Long> {
    List<PetStarSlot> findByPetIdOrderByImageIndexAscSlotPositionAsc(Long petId);
    List<PetStarSlot> findByPetIdAndImageIndex(Long petId, int imageIndex);

    long countByPetId(Long petId);
}

