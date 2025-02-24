package com.remake.poki.service;

import com.remake.poki.dto.UserPetDTO;
import com.remake.poki.model.Pet;
import com.remake.poki.model.User;
import com.remake.poki.repo.PetRepository;
import com.remake.poki.repo.UserPetRepository;
import com.remake.poki.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserPetService {
    final
    UserPetRepository userPetRepository;

    @Autowired
    PetRepository petRepository;
    @Autowired
    UserRepository userRepository;

    public UserPetService(UserPetRepository userPetRepository) {
        this.userPetRepository = userPetRepository;
    }

    public List<UserPetDTO> getUserPets(Long userId) {
        return userPetRepository.getListUserPets(userId);
    }

    public UserPetDTO getInfoMatch(Long userId, Long petId, Long ePetId) {
        Pet pet = petRepository.findById(ePetId).get();
        UserPetDTO userPetDTO = userPetRepository.getInfoMatch(userId, petId);
        User user = userRepository.findById(userId).get();
        user.setPetId(petId);
        userRepository.save(user);
        if(!pet.getElementType().equals(userPetDTO.getElementOther())){
            userPetDTO.setWeaknessValue(BigDecimal.valueOf(1));
        }
        return userPetDTO;
    }
}
