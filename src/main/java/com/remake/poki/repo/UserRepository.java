package com.remake.poki.repo;

import aj.org.objectweb.asm.commons.Remapper;
import com.remake.poki.dto.UserDTO;
import com.remake.poki.dto.UserRoomDTO;
import com.remake.poki.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT new com.remake.poki.dto.UserRoomDTO(u, cp, p, ep) " +
            "FROM Pet p " +
            "JOIN User u ON u.petId = p.id AND u.id = :userId "+
            "JOIN EnemyPet ep ON ep.idPet = :enemyPetId "+
            "LEFT JOIN CountPass cp ON cp.idUser = u.id AND ep.idPet = cp.idPet" )
    UserRoomDTO findInfoRoom(Long userId, Long enemyPetId);

    Optional<User> findByUserAndPassword(String user, String password);

    Optional<User> findByUser(String user);
}
