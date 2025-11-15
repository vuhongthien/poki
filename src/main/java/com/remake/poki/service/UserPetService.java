package com.remake.poki.service;

import com.remake.poki.dto.UserPetDTO;
import com.remake.poki.model.Pet;
import com.remake.poki.model.User;
import com.remake.poki.model.UserPet;
import com.remake.poki.repo.PetRepository;
import com.remake.poki.repo.UserPetRepository;
import com.remake.poki.repo.UserRepository;
import com.remake.poki.util.Calculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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
        List<UserPetDTO> pets = userPetRepository.getListUserPets(userId);
        return pets.stream()
                .map(Calculator::calculateStats)
                .collect(Collectors.toList());
    }

    public UserPetDTO getInfoMatch(Long userId, Long petId, Long ePetId) {
        Pet pet = petRepository.findById(ePetId).get();
        UserPetDTO userPetDTO = Calculator.calculateStats(userPetRepository.getInfoMatch(userId, petId));
        User user = userRepository.findById(userId).get();
        user.setPetId(petId);
        userRepository.save(user);
        if(!pet.getElementType().equals(userPetDTO.getElementOther())){
            userPetDTO.setWeaknessValue(BigDecimal.valueOf(1));
        }
        return userPetDTO;
    }

    public UserPetDTO updatePets(Long userId, Long petId) {
        UserPet userPet = userPetRepository.findByUserIdAndPetId(userId, petId)
                .orElseThrow(() -> new RuntimeException("UserPet not found"));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        if (userPet.getLevel() >= pet.getMaxLevel()) {
            throw new RuntimeException("Pet has reached maximum level");
        }

        userPet.setLevel(userPet.getLevel() + 1);
        userPetRepository.save(userPet);
        return Calculator.calculateStats(userPetRepository.getUserPet(userId,petId));
    }

    public UserPetDTO getUserPetsHT(Long userId, Long petId) {
        return Calculator.calculateStats(userPetRepository.getUserPet(userId, petId));
    }
}
