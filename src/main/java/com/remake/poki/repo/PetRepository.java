package com.remake.poki.repo;

import com.remake.poki.dto.PetDTO;
import com.remake.poki.dto.PetEnemyDTO;
import com.remake.poki.dto.UserPetDTO;
import com.remake.poki.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    @Query(value = "SELECT new com.remake.poki.dto.PetDTO( p.id, p.skillCardId, p.name, p.elementType, p.maxLevel, ew.element, ps.hp, ps.attack, ps.mana, ps.weaknessValue, p.des) " +
            "FROM Pet p " +
            "JOIN PetStats ps ON p.id = ps.petId " +
            "JOIN ElementWeakness ew ON p.elementType = ew.weakAgainst " +
            "WHERE ps.level = 1 ORDER BY p.id ASC")
    List<PetDTO> findAllPet();

    @Query(value = "SELECT new com.remake.poki.dto.PetEnemyDTO(p.id, p.name, ep.lever, ep.leverDisplay, COALESCE(cp.count, 0), ep.requestPass, ep.requestAttack) " +
            "FROM Pet p " +
            "LEFT JOIN CountPass cp ON p.id = cp.idPet AND cp.idUser = :userId " +
            "JOIN EnemyPet ep ON ep.idPet = p.id AND ep.idGroupPet = :groupId")
    List<PetEnemyDTO> getEnemyPets(Long userId, Long groupId);

    @Query(value = "SELECT new com.remake.poki.dto.UserPetDTO(p, ep, ps, ew) " +
            "FROM Pet p " +
            "JOIN EnemyPet ep ON p.id = ep.idPet AND p.id = :petEId " +
            "JOIN PetStats ps ON ep.idPet = ps.petId " +
            "LEFT JOIN ElementWeakness ew ON p.elementType = ew.weakAgainst")
    UserPetDTO getInfoEPet( Long petEId);


    @Query("SELECT p FROM Pet p WHERE p.flagLegend = true ORDER BY p.no ASC")
    List<Pet> findLegendPetsOrderByNoAsc();

}
