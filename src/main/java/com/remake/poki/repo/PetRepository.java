package com.remake.poki.repo;

import com.remake.poki.dto.PetDTO;
import com.remake.poki.dto.PetEnemyDTO;
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
            "WHERE p.maxLevel = ps.level OR p.maxLevel IS NULL")
    List<PetDTO> findAllPet();

    @Query(value = "SELECT new com.remake.poki.dto.PetEnemyDTO(p.id, p.name, ep.lever, ep.leverDisplay, COALESCE(cp.count, 0), ep.requestPass, ep.requestAttack) " +
            "FROM Pet p " +
            "LEFT JOIN CountPass cp ON p.id = cp.idPet AND cp.idUser = :userId " +
            "JOIN EnemyPet ep ON ep.idPet = p.id AND ep.idGroupPet = :groupId")
    List<PetEnemyDTO> getEnemyPets(Long userId, Long groupId);

}
