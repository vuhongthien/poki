package com.remake.poki.repo;

import com.remake.poki.enums.ElementType;
import com.remake.poki.model.Stone;
import com.remake.poki.model.StoneUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoneRepository extends JpaRepository<Stone, Long> {
    Optional<Stone> findByElementTypeAndLever(ElementType element, Integer level);
}
