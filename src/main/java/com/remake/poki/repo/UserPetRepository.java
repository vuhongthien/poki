package com.remake.poki.repo;

import com.remake.poki.dto.UserPetDTO;
import com.remake.poki.model.UserPet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPetRepository extends JpaRepository<UserPet, Long> {
    @Query(value = "SELECT new com.remake.poki.dto.UserPetDTO(p,up, ps, ew, u, sc) " +
            "FROM Pet p, UserPet up,PetStats ps, ElementWeakness ew, User u, SkillCard sc " +
            "WHERE p.id = up.petId " +
            "AND up.userId = u.id " +
            "AND up.petId = ps.petId " +
            "AND p.elementType = ew.weakAgainst " +
            "AND p.skillCardId = sc.id " +
            "AND u.id = :userId")
    List<UserPetDTO> getListUserPets(Long userId);
}
