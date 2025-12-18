package com.remake.poki.repo;

import aj.org.objectweb.asm.commons.Remapper;
import com.remake.poki.dto.UserDTO;
import com.remake.poki.dto.UserRoomDTO;
import com.remake.poki.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT new com.remake.poki.dto.UserRoomDTO(u, cp, p, ep) " +
            "FROM Pet p " +
            "JOIN User u ON u.petId = p.id AND u.id = :userId "+
            "JOIN EnemyPet ep ON ep.idPet = :enemyPetId "+
            "LEFT JOIN CountPass cp ON cp.idUser = u.id AND ep.idPet = cp.idPet" )
    UserRoomDTO findInfoRoom(Long userId, Long enemyPetId);

    @Query(value = "SELECT new com.remake.poki.dto.UserRoomDTO(u, null, p, ep) " +
            "FROM Pet p " +
            "JOIN User u ON u.petId = p.id AND u.id = :userId "+
            "JOIN EnemyPet ep ON ep.idPet = :enemyPetId WHERE p.flagLegend = true")
    UserRoomDTO findInfoRoomHT(Long userId, Long enemyPetId);

    Optional<User> findByUserAndPassword(String user, String password);

    Optional<User> findByUser(String user);

    @Modifying
    @Query("UPDATE User u SET u.wheelDay = 2 WHERE u.wheelDay < 2")
    int resetWheelDayForUsersBelow2();

    boolean existsByUser(String user);
    boolean existsByName(String name);  // Kiểm tra tên nhân vật trùng

    List<User> findAllByOrderByIdDesc();
    List<User> findAllByOrderByCreatedAtDesc();
    List<User> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime cutoffDate);
    @Query(value = """
        SELECT 
            u.id,
            u.name,
            u.pet_id,
            u.avt_id,
            u.lever,
            COUNT(up.id) as pet_count
        FROM users u
        LEFT JOIN user_pet up ON u.id = up.user_id
        GROUP BY u.id, u.name, u.pet_id, u.avt_id, u.lever
        ORDER BY u.lever DESC, pet_count DESC
        LIMIT 9
        """, nativeQuery = true)
    List<Object[]> findTop9UsersByLevelAndPetCount();
}
