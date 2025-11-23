package com.remake.poki.repo;

import com.remake.poki.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RankingRepository extends JpaRepository<User, Long> {
    
    @Query(value = """
        SELECT 
            u.id as userId,
            u.name as userName,
            u.pet_id as currentPetId,
            u.avt_id as avtId,
            u.lever as level,
            COALESCE(SUM(ps.attack + ps.hp), 0) as totalCombatPower
        FROM users u
        LEFT JOIN user_pet up ON up.user_id = u.id
        LEFT JOIN pet_stats ps ON ps.pet_id = up.pet_id AND ps.level = up.level
        GROUP BY u.id, u.name, u.pet_id, u.lever
        ORDER BY totalCombatPower DESC
        LIMIT 9
    """, nativeQuery = true)
    List<Object[]> getTop9Users();

    @Query(value = """
        SELECT DISTINCT u.id
        FROM users u
        LEFT JOIN user_pet up ON up.user_id = u.id
        WHERE up.user_id IS NOT NULL
    """, nativeQuery = true)
    List<Long> getAllUserIdsWithPets();
}
