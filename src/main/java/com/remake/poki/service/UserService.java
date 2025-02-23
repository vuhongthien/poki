package com.remake.poki.service;

import com.remake.poki.dto.UserDTO;
import com.remake.poki.dto.UserRoomDTO;
import com.remake.poki.model.Pet;
import com.remake.poki.repo.PetRepository;
import com.remake.poki.repo.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    PetRepository petRepository;

    private final
    ModelMapper modelMapper;
    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public UserDTO getMoney(Long userId) {
        return userRepository.findById(userId)
                .map(user -> modelMapper.map(user, UserDTO.class))
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
    }

    public UserRoomDTO getInfoRoom(Long userId, Long enemyPetId) {
        UserRoomDTO userRoomDTO = userRepository.findInfoRoom(userId,enemyPetId);
        Pet pet = petRepository.findById(enemyPetId).orElseThrow();
        userRoomDTO.setNameEnemyPetId(pet.getName());
        return userRoomDTO;
    }
}
