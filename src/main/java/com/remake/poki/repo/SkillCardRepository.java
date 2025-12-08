package com.remake.poki.repo;

import com.remake.poki.model.SkillCard;
import com.remake.poki.model.Stone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillCardRepository extends JpaRepository<SkillCard, Long> {
}
