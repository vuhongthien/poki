package com.remake.poki.service;

import com.remake.poki.dto.UserPetDTO;
import com.remake.poki.repo.UserPetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPetService {
    final
    UserPetRepository userPetRepository;

    public UserPetService(UserPetRepository userPetRepository) {
        this.userPetRepository = userPetRepository;
    }

    public List<UserPetDTO> getUserPets(Long userId) {
        return userPetRepository.getListUserPets(userId);
    }
}
