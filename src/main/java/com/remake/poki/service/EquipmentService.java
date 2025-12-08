package com.remake.poki.service;

import com.remake.poki.dto.AvatarEquipmentDTO;
import com.remake.poki.dto.PetEquipmentDTO;
import com.remake.poki.model.*;
import com.remake.poki.repo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EquipmentService {

    private final UserRepository userRepository;
    private final UserPetRepository userPetRepository;
    private final UserAvatarRepository userAvatarRepository;
    private final PetRepository petRepository;
    private final AvatarRepository avatarRepository;

    /**
     * Lấy danh sách pet của user với phân trang
     */
    public List<PetEquipmentDTO> getUserPets(Long userId, int page, int size) {
        log.info("Getting pets for user {} - page: {}, size: {}", userId, page, size);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
        List<UserPet> userPets = userPetRepository.findByUserId(userId);
        userPets.sort((a, b) -> Long.compare(b.getId(), a.getId()));
        // Phân trang thủ công
        int start = page * size;
        int end = Math.min(start + size, userPets.size());

        if (start >= userPets.size()) {
            return new ArrayList<>();
        }

        List<UserPet> pagedPets = userPets.subList(start, end);

        return pagedPets.stream().map(userPet -> {
            Pet pet = petRepository.findById(userPet.getPetId())
                    .orElse(null);

            if (pet == null) return null;

            PetEquipmentDTO dto = new PetEquipmentDTO();
            dto.setId(userPet.getId());
            dto.setPetId(pet.getId());
            dto.setName(pet.getName());
            dto.setLevel(userPet.getLevel());
            dto.setElementType(pet.getElementType());

            // Tính toán stats dựa trên level (có thể tùy chỉnh công thức)
            int baseHp = 100;
            int baseAttack = 50;
            int baseMana = 80;

            dto.setHp(baseHp + (userPet.getLevel() * 10));
            dto.setAttack(baseAttack + (userPet.getLevel() * 5));
            dto.setMana(baseMana + (userPet.getLevel() * 8));

            // Check xem pet này có đang được trang bị không
            boolean isEquipped = false;
            if (user.getPetId() != null && pet.getId() != null) {
                isEquipped = user.getPetId().equals(pet.getId());
                log.debug("Pet {} - user.petId: {}, pet.id: {}, isEquipped: {}",
                        pet.getName(), user.getPetId(), pet.getId(), isEquipped);
            }
            dto.setEquipped(isEquipped);

            return dto;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Lấy danh sách avatar của user với phân trang
     */
    public List<AvatarEquipmentDTO> getUserAvatars(Long userId, int page, int size) {
        log.info("Getting avatars for user {} - page: {}, size: {}", userId, page, size);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserAvatar> userAvatars = userAvatarRepository.findByUserId(userId);
        userAvatars.sort((a, b) -> Long.compare(b.getId(), a.getId()));
        // Phân trang thủ công
        int start = page * size;
        int end = Math.min(start + size, userAvatars.size());

        if (start >= userAvatars.size()) {
            return new ArrayList<>();
        }

        List<UserAvatar> pagedAvatars = userAvatars.subList(start, end);

        return pagedAvatars.stream().map(userAvatar -> {
            Avatar avatar = avatarRepository.findById(userAvatar.getAvatarId())
                    .orElse(null);

            if (avatar == null) return null;

            AvatarEquipmentDTO dto = new AvatarEquipmentDTO();
            dto.setId(userAvatar.getId());
            dto.setAvatarId(avatar.getId());
            dto.setName(avatar.getName());
            dto.setHp(avatar.getHp());
            dto.setAttack(avatar.getAttack());
            dto.setMana(avatar.getMana());
            dto.setBlind(avatar.getBlind());

            // Check xem avatar này có đang được trang bị không
            boolean isEquipped = false;
            if (user.getAvtId() != null && avatar.getId() != null) {
                isEquipped = user.getAvtId().equals(avatar.getId());
                log.debug("Avatar {} - user.avtId: {}, avatar.id: {}, isEquipped: {}",
                        avatar.getName(), user.getAvtId(), avatar.getId(), isEquipped);
            }
            dto.setEquipped(isEquipped);

            return dto;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Trang bị pet cho user
     */
    @Transactional
    public Map<String, Object> equipPet(Long userId, Long petId) {
        log.info("Equipping pet {} for user {}", petId, userId);

        // Kiểm tra user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Kiểm tra user có pet này không
        UserPet userPet = userPetRepository.findByUserIdAndPetId(userId, petId)
                .orElseThrow(() -> new RuntimeException("User doesn't own this pet"));

        // Cập nhật petId trong bảng User
        user.setPetId(petId);
        userRepository.save(user);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Pet equipped successfully");
        result.put("equippedPetId", petId);

        log.info("Pet {} equipped successfully for user {}", petId, userId);
        return result;
    }

    /**
     * Trang bị avatar cho user
     */
    @Transactional
    public Map<String, Object> equipAvatar(Long userId, Long avatarId) {
        log.info("Equipping avatar {} for user {}", avatarId, userId);

        // Kiểm tra user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Kiểm tra user có avatar này không
        userAvatarRepository.findByUserIdAndAvatarId(userId, avatarId)
                .orElseThrow(() -> new RuntimeException("User doesn't own this avatar"));

        // Cập nhật avtId trong bảng User
        user.setAvtId(avatarId);
        userRepository.save(user);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Avatar equipped successfully");
        result.put("equippedAvatarId", avatarId);

        log.info("Avatar {} equipped successfully for user {}", avatarId, userId);
        return result;
    }

    /**
     * Đếm số lượng pet và avatar của user
     */
    public Map<String, Integer> getEquipmentCount(Long userId) {
        int petCount = userPetRepository.countByUserId(userId);
        int avatarCount = userAvatarRepository.countByUserId(userId);

        Map<String, Integer> count = new HashMap<>();
        count.put("petCount", petCount);
        count.put("avatarCount", avatarCount);
        count.put("petPages", (int) Math.ceil(petCount / 10.0));
        count.put("avatarPages", (int) Math.ceil(avatarCount / 3.0));

        return count;
    }

    /**
     * Debug: Lấy thông tin pet và avatar đang trang bị
     */
    public Map<String, Object> getCurrentEquipment(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("currentPetId", user.getPetId());
        result.put("currentAvatarId", user.getAvtId());

        // Lấy thông tin chi tiết nếu có
        if (user.getPetId() != null) {
            petRepository.findById(user.getPetId()).ifPresent(pet -> {
                Map<String, Object> petInfo = new HashMap<>();
                petInfo.put("id", pet.getId());
                petInfo.put("name", pet.getName());
                result.put("currentPet", petInfo);
            });
        }

        if (user.getAvtId() != null) {
            avatarRepository.findById(user.getAvtId()).ifPresent(avatar -> {
                Map<String, Object> avatarInfo = new HashMap<>();
                avatarInfo.put("id", avatar.getId());
                avatarInfo.put("name", avatar.getName());
                result.put("currentAvatar", avatarInfo);
            });
        }

        log.info("Current equipment for user {}: petId={}, avatarId={}",
                userId, user.getPetId(), user.getAvtId());

        return result;
    }
}