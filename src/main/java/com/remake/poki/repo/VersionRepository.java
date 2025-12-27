package com.remake.poki.repo;

import com.remake.poki.model.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VersionRepository extends JpaRepository<Version, Long> {

    /**
     * Lấy version info mới nhất (ID lớn nhất)
     */
    Optional<Version> findFirstByOrderByIdDesc();

    /**
     * Lấy version info theo version string
     */
    Optional<Version> findByVersion(String version);

    /**
     * Kiểm tra version có tồn tại không
     */
    boolean existsByVersion(String version);
}