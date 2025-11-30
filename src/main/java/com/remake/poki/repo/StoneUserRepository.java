package com.remake.poki.repo;

import com.remake.poki.dto.StoneDTO;
import com.remake.poki.enums.ElementType;
import com.remake.poki.model.StoneUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoneUserRepository extends JpaRepository<StoneUser, Long> {

    @Query("""
        SELECT new com.remake.poki.dto.StoneDTO(
            su.idUser,
            s.id,
            su.count,
            s.name,
            s.lever,
            s.elementType
        )
        FROM StoneUser su
        JOIN Stone s ON su.idStone = s.id
        WHERE su.idUser = :userId
        ORDER BY s.elementType ASC, s.lever ASC
    """)
    List<StoneDTO> findAllByUserId(@Param("userId") Long userId);

    Optional<StoneUser> findByIdUserAndIdStone(Long idUser, Long idStone);

    List<StoneUser> findByIdUser(Long userId);

    List<StoneUser> findByIdUserAndCountGreaterThan(Long idUser, int count);
}
