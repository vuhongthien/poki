package com.remake.poki.repo;

import com.remake.poki.dto.StoneDTO;
import com.remake.poki.enums.ElementType;
import com.remake.poki.model.Stone;
import com.remake.poki.model.StoneUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoneRepository extends JpaRepository<Stone, Long> {
    Optional<Stone> findByElementTypeAndLever(ElementType element, Integer level);
    List<Stone> findByElementType(ElementType elementType);

    Optional<Stone> findByElementTypeAndLever(ElementType elementType, int lever);

    List<Stone> findByLever(int lever);

}
