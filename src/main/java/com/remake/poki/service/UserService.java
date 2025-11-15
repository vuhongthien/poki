package com.remake.poki.service;

import com.remake.poki.dto.*;
import com.remake.poki.model.Pet;
import com.remake.poki.model.User;
import com.remake.poki.repo.PetRepository;
import com.remake.poki.repo.UserRepository;
import com.remake.poki.request.UpdateStarRequest;
import com.remake.poki.response.UpdateStarResponse;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

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
        if (userId == 0) {
            userId = 1L;
        }
        Long finalUserId = userId;
        return userRepository.findById(userId)
                .map(user -> modelMapper.map(user, UserDTO.class))
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + finalUserId));
    }

    public UserRoomDTO getInfoRoom(Long userId, Long enemyPetId) {
        UserRoomDTO userRoomDTO = userRepository.findInfoRoom(userId, enemyPetId);
        if(userRoomDTO == null){
            userRoomDTO = userRepository.findInfoRoomHT(userId, enemyPetId);
        }
        Pet pet = petRepository.findById(enemyPetId).orElseThrow();
        userRoomDTO.setElementType(pet.getElementType().name());
        return userRoomDTO;
    }

    public UserDTO login(LoginDTO request) {
        User user = userRepository.findByUserAndPassword(request.getUser(), request.getPassword())
                .orElseThrow(() -> new NoSuchElementException("fail info login: " + request.getUser()));
        ;
        return getMoney(user.getId());
    }

    public UserDTO getUser(String userName) {
        if (userName == null) {
            userName = "";
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

    public String upCT(Long userId, int requestAttack) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        user.setRequestAttack(user.getRequestAttack() + requestAttack);
        userRepository.save(user);
        return "down energy successfully";
    }

    @Transactional
    public DeductGoldResponseDTO deductGold(DeductGoldRequestDTO request) {
        try {
            validateDeductGoldRequest(request);
            User user = findUserById(request.getUserId());
            if (user.getGold() < request.getAmount()) {
                return new DeductGoldResponseDTO(
                        false,
                        "Không đủ gold để thực hiện thao tác này",
                        user.getGold()
                );
            }
            int newGold = user.getGold() - request.getAmount();
            user.setGold(newGold);
            userRepository.save(user);
            return new DeductGoldResponseDTO(
                    true,
                    "Đã trừ " + request.getAmount() + " gold thành công",
                    newGold
            );

        } catch (Exception e) {
            return new DeductGoldResponseDTO(false, "User không tồn tại", 0);
        }
    }

    private void validateDeductGoldRequest(DeductGoldRequestDTO request) {
        if (request.getUserId() <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Số vàng phải lớn hơn 0");
        }

        if (request.getReason() == null || request.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("Lý do trừ vàng không được để trống");
        }
    }

    private User findUserById(int userId) {
        return userRepository.findById((long) userId).orElseThrow();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User updateWheel(Long userId, int wheelAmount) {
        User user = findById(userId);
        if (user != null) {
            user.setWheel(wheelAmount);
            return save(user);
        }
        return null;
    }

    @Transactional
    public UpdateStarResponse updateStar(UpdateStarRequest request) {
        UpdateStarResponse response = new UpdateStarResponse();

        try {
            // Tìm user
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Cập nhật sao theo loại
            switch (request.getStarType().toLowerCase()) {
                case "white":
                    user.setStarWhite(user.getStarWhite() + request.getAmount());
                    break;
                case "blue":
                    user.setStarBlue(user.getStarBlue() + request.getAmount());
                    break;
                case "red":
                    user.setStarRed(user.getStarRed() + request.getAmount());
                    break;
                default:
                    response.setSuccess(false);
                    response.setMessage("Invalid star type: " + request.getStarType());
                    return response;
            }

            // Lưu user
            user = userRepository.save(user);

            // Trả về response thành công
            response.setSuccess(true);
            response.setMessage("Star updated successfully");
            response.setStarWhite(user.getStarWhite());
            response.setStarBlue(user.getStarBlue());
            response.setStarRed(user.getStarRed());

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error updating star: " + e.getMessage());
        }

        return response;
    }
}
