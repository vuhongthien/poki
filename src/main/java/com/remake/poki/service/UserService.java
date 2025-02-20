package com.remake.poki.service;

import com.remake.poki.dto.UserDTO;
import com.remake.poki.repo.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserService {
    private final UserRepository userRepository;

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

}
