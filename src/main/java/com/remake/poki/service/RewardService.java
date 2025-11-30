package com.remake.poki.service;

import com.remake.poki.enums.ElementType;
import com.remake.poki.model.*;
import com.remake.poki.repo.*;
import com.remake.poki.request.PetRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RewardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPetRepository userPetRepository;

    @Autowired
    private UserStoneRepository userStoneRepository;

    @Autowired
    private StoneRepository stoneRepository;

    @Autowired
    private CountPassRepository countPassRepository;

    @Autowired
    private AuditRewardRepository auditRewardRepository;

    @Autowired
    private PetRepository petRepository;

    /**
     * Thêm Pet cho User
     */
    @Transactional
    public void addPetToUser(Long userId, Long petId, int requestAttack) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        user.setRequestAttack(user.getRequestAttack()+requestAttack);
        userRepository.save(user);

//        // Kiểm tra user đã có pet này chưa
//        if (userPetRepository.existsByUserIdAndPetId(userId, petId)) {
//            throw new RuntimeException("User already has this pet");
//        }
        if (auditRewardRepository.existsByUserIdAndPetId(userId, petId)) {
            throw new RuntimeException("User already has this pet");
        }
        Pet pet = petRepository.findById(petId).orElse(null);
        if (pet == null) {
            throw new RuntimeException("Pet not found");
        }

        AuditReward auditReward = new AuditReward();
        auditReward.setUserId(userId);
        auditReward.setPetId(petId);
        auditReward.setCreatedAt(LocalDateTime.now());
        auditRewardRepository.save(auditReward);

        UserPet userPet = new UserPet();
        userPet.setUserId(userId);
        if(pet.getChildId() != null){
            userPet.setPetId(pet.getChildId());
        }else{
            userPet.setPetId(petId);
        }
        userPetRepository.save(userPet);
    }

    /**
     * Thêm Stone cho User
     */
    @Transactional
    public void addStoneToUser(Long userId, String element, Integer level, Integer quantity) {

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        ElementType elementType;
        try {
            elementType = ElementType.valueOf(element.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid element type: " + element);
        }

        Stone stone = stoneRepository.findByElementTypeAndLever(elementType, level).orElse(null);
        if (stone == null) {
            throw new RuntimeException("Stone not found for element: " + element + " level: " + level);
        }

        // ✅ SỬA: Tìm theo CẢ userId VÀ stoneId
        StoneUser existingStone = userStoneRepository
                .findByIdUserAndIdStone(userId, stone.getId())  // ⬅️ THÊM userId
                .orElse(null);

        if (existingStone != null) {
            // Cộng dồn số lượng
            existingStone.setCount(existingStone.getCount() + quantity);
            userStoneRepository.save(existingStone);
            System.out.println("Updated stone: userId=" + userId + ", stoneId=" + stone.getId() + ", newCount=" + existingStone.getCount());
        } else {
            // Tạo mới
            StoneUser newStone = new StoneUser();
            newStone.setIdUser(userId);
            newStone.setIdStone(stone.getId());
            newStone.setCount(quantity);
            userStoneRepository.save(newStone);
            System.out.println("Created stone: userId=" + userId + ", stoneId=" + stone.getId() + ", count=" + quantity);
        }
    }

    public void upCountPass(Long userId, PetRequest request) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        CountPass countPass = countPassRepository.findByIdUserAndIdPet(userId, request.getPetId()).orElse(null);
        if (countPass == null) {
            countPass = new CountPass();
            countPass.setCount(1);
            countPass.setIdUser(userId);
            countPass.setIdPet(request.getPetId());
        }else{
            countPass.setCount(countPass.getCount() + 1);
        }
        countPassRepository.save(countPass);
    }
}
