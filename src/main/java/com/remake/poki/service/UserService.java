package com.remake.poki.service;

import com.remake.poki.dto.LoginDTO;
import com.remake.poki.dto.UserDTO;
import com.remake.poki.dto.UserPetDTO;
import com.remake.poki.dto.UserRoomDTO;
import com.remake.poki.model.Pet;
import com.remake.poki.model.User;
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
        if(userId==0){
            userId=1L;
        }
        Long finalUserId = userId;
        return userRepository.findById(userId)
                .map(user -> modelMapper.map(user, UserDTO.class))
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + finalUserId));
    }

    public UserRoomDTO getInfoRoom(Long userId, Long enemyPetId) {
        UserRoomDTO userRoomDTO = userRepository.findInfoRoom(userId, enemyPetId);
        Pet pet = petRepository.findById(enemyPetId).orElseThrow();
        userRoomDTO.setElementType(pet.getElementType().name());
        return userRoomDTO;
    }

    public UserDTO login(LoginDTO request) {
        User user = userRepository.findByUserAndPassword(request.getUser(), request.getPassword())
                .orElseThrow(() -> new NoSuchElementException("fail info login: " + request.getUser()));;
        return getMoney(user.getId());
    }

    public UserDTO getUser(String userName) {
        if(userName==null){
            userName="";
        }
        String finalUserName = userName;
        return userRepository.findByUser(finalUserName)
                .map(user -> modelMapper.map(user, UserDTO.class))
                .orElseThrow(() -> new NoSuchElementException("User not found with user name: " + finalUserName));
    }

    public String downEnergy(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        user.setEnergy(user.getEnergy() - 1);
        userRepository.save(user);
        return "down energy successfully";
    }
}
