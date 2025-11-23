package com.remake.poki.repo;

import com.remake.poki.model.PetStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetStatsRepository extends JpaRepository<PetStats, Long> {
    List<PetStats> findAllByPetId(Long petId);
    Optional<PetStats> findByPetIdAndLevel(Long petId, int level);
}
