package com.remake.poki.repo;

import com.remake.poki.model.UserMilestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMilestoneRepository extends JpaRepository<UserMilestone, Long> {
    
    Optional<UserMilestone> findByUserIdAndMilestoneId(Long userId, Long milestoneId);
}
