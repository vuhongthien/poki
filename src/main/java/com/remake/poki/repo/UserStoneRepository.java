package com.remake.poki.repo;

import com.remake.poki.model.StoneUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserStoneRepository extends JpaRepository<StoneUser, Long> {

    Optional<StoneUser> findByIdStone(Long id);
}
