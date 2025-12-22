package com.remake.poki.repo;

import com.remake.poki.model.RechargeMilestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RechargeMilestoneRepository extends JpaRepository<RechargeMilestone, Long> {

    List<RechargeMilestone> findByIsActiveTrueOrderBySortOrder();
}
