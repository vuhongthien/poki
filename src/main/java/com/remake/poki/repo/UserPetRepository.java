package com.remake.poki.repo;

import com.remake.poki.dto.UserPetDTO;
import com.remake.poki.model.UserPet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPetRepository extends JpaRepository<UserPet, Long> {
    @Query(value = "SELECT new com.remake.poki.dto.UserPetDTO(p, up, ps, ew, u, sc) " +
            "FROM Pet p " +
            "JOIN UserPet up ON p.id = up.petId " +
            "JOIN PetStats ps ON up.petId = ps.petId AND up.level = ps.level " +
            "JOIN User u ON up.userId = u.id " +
            "LEFT JOIN ElementWeakness ew ON p.elementType = ew.weakAgainst " +
            "LEFT JOIN SkillCard sc ON p.skillCardId = sc.id " +
            "WHERE up.userId = :userId")
    List<UserPetDTO> getListUserPets(Long userId);

    @Query(value = "SELECT new com.remake.poki.dto.UserPetDTO(p, up, ps, ew, u, sc) " +
            "FROM Pet p " +
            "JOIN UserPet up ON p.id = up.petId AND p.id = :petId " +
            "JOIN PetStats ps ON up.petId = ps.petId AND up.level = ps.level " +
            "JOIN User u ON up.userId = u.id AND u.id = :userId " +
            "JOIN ElementWeakness ew ON p.elementType = ew.weakAgainst " +
            "LEFT JOIN SkillCard sc ON p.skillCardId = sc.id ")
    UserPetDTO getInfoMatch(Long userId, Long petId);

    boolean existsByUserIdAndPetId(Long userId, Long petId);
}
