package com.remake.poki.repo;

import com.remake.poki.model.StoneUser;
import com.remake.poki.model.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VersionRepository extends JpaRepository<Version, Long> {
    Optional<Version> findByVersion(String version);
}
