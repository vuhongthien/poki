package com.remake.poki.repo;

import com.remake.poki.model.PetStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetStatsRepository extends JpaRepository<PetStats, Long> {
    List<PetStats> findAllByPetId(Long petId);
}
