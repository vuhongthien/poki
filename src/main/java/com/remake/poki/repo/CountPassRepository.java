package com.remake.poki.repo;

import com.remake.poki.model.CountPass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountPassRepository extends JpaRepository<CountPass, Long> {
    boolean existsByIdUserAndIdPet(Long idUser, Long idPet);

    Optional<CountPass> findByIdUserAndIdPet(Long userId, Long petId);
}
