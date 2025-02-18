package com.remake.poki.repo;

import com.remake.poki.dto.GroupDTO;
import com.remake.poki.model.GroupPet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupPetRepository extends JpaRepository<GroupPet, Long> {

    @Query(value = "SELECT new com.remake.poki.dto.GroupDTO( p.id, p.name ) " +
            "FROM GroupPet p ORDER BY p.step")
    List<GroupDTO> getGroupPet(Long userId);
}
