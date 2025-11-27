package com.remake.poki.repo;

import com.remake.poki.model.Avatar;
import com.remake.poki.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {
}
