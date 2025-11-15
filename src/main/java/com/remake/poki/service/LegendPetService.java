package com.remake.poki.service;

import com.remake.poki.dto.*;
import com.remake.poki.model.*;
import com.remake.poki.repo.*;
import com.remake.poki.request.UnlockPetRequest;
import com.remake.poki.response.UnlockPetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LegendPetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetStarSlotRepository petStarSlotRepository;

    @Autowired
    private UserPetStarRepository userPetStarRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPetRepository userPetRepository;

    /**
     * Lấy thông tin Pet Huyền Thoại cho user
     */
    public LegendPetDTO getLegendPetInfo(Long userId, Long petId) {
        // Lấy thông tin pet
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        if (!pet.isFlagLegend()) {
            throw new RuntimeException("Pet is not a legend pet");
        }

        // Lấy tất cả slot sao của pet
        List<PetStarSlot> allSlots = petStarSlotRepository
                .findByPetIdOrderByImageIndexAscSlotPositionAsc(petId);

        // Lấy trạng thái khảm sao của user
        List<UserPetStar> userStars = userPetStarRepository
                .findByUserIdAndPetId(userId, petId);
        Map<Long, Boolean> inlaidMap = userStars.stream()
                .collect(Collectors.toMap(UserPetStar::getSlotId, UserPetStar::isInlaid));

        // Lấy thông tin số sao hiện có của user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Nhóm slots theo imageIndex
        Map<Integer, List<PetStarSlot>> slotsByImage = allSlots.stream()
                .collect(Collectors.groupingBy(PetStarSlot::getImageIndex));

        // Tạo danh sách ImageHT
        List<ImageHTDTO> images = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            List<PetStarSlot> imageSlots = slotsByImage.getOrDefault(i, new ArrayList<>());

            List<StarSlotDTO> starSlots = imageSlots.stream()
                    .map(slot -> {
                        StarSlotDTO dto = new StarSlotDTO();
                        dto.setSlotId(slot.getId());
                        dto.setStarType(slot.getStarType());
                        dto.setSlotPosition(slot.getSlotPosition());
                        dto.setRequiredStarCount(slot.getRequiredStarCount());
                        dto.setInlaid(inlaidMap.getOrDefault(slot.getId(), false));

                        // Kiểm tra xem user có đủ sao để khảm không
                        boolean canInlay = false;
                        if (!dto.isInlaid()) {
                            switch (slot.getStarType()) {
                                case 1: // Sao trắng
                                    canInlay = user.getStarWhite() >= slot.getRequiredStarCount();
                                    break;
                                case 2: // Sao xanh
                                    canInlay = user.getStarBlue() >= slot.getRequiredStarCount();
                                    break;
                                case 3: // Sao đỏ
                                    canInlay = user.getStarRed() >= slot.getRequiredStarCount();
                                    break;
                            }
                        }
                        dto.setCanInlay(canInlay);

                        return dto;
                    })
                    .collect(Collectors.toList());

            ImageHTDTO imageDTO = new ImageHTDTO();
            imageDTO.setImageIndex(i);
            imageDTO.setStarSlots(starSlots);
            images.add(imageDTO);
        }

        // Tính tổng số sao
        int totalStars = allSlots.size();
        int inlaidStars = (int) inlaidMap.values().stream().filter(Boolean::booleanValue).count();

        // Kiểm tra xem đã unlock pet chưa
        boolean unlocked = userPetRepository.existsByUserIdAndPetId(userId, petId);

        LegendPetDTO dto = new LegendPetDTO();
        dto.setPetId(petId);
        dto.setName(pet.getName());
        dto.setDescription(pet.getDes());
        dto.setTotalStars(totalStars);
        dto.setInlaidStars(inlaidStars);
        dto.setUnlocked(unlocked);
        dto.setImages(images);

        return dto;
    }

    /**
     * Khảm sao vào slot
     */
    @Transactional
    public InlayStarResponse inlayStar(InlayStarRequest request) {
        InlayStarResponse response = new InlayStarResponse();

        try {
            // Lấy thông tin user
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Lấy thông tin slot
            PetStarSlot slot = petStarSlotRepository.findById(request.getSlotId())
                    .orElseThrow(() -> new RuntimeException("Slot not found"));

            // Kiểm tra xem đã khảm chưa
            Optional<UserPetStar> existingStar = userPetStarRepository
                    .findByUserIdAndSlotId(request.getUserId(), request.getSlotId());

            if (existingStar.isPresent() && existingStar.get().isInlaid()) {
                response.setSuccess(false);
                response.setMessage("Star already inlaid");
                return response;
            }

            // Kiểm tra số sao
            int currentStars = 0;
            switch (slot.getStarType()) {
                case 1: // Sao trắng
                    currentStars = user.getStarWhite();
                    break;
                case 2: // Sao xanh
                    currentStars = user.getStarBlue();
                    break;
                case 3: // Sao đỏ
                    currentStars = user.getStarRed();
                    break;
            }

            if (currentStars < slot.getRequiredStarCount()) {
                response.setSuccess(false);
                response.setMessage("Not enough stars");
                return response;
            }

            // Trừ số sao
            switch (slot.getStarType()) {
                case 1:
                    user.setStarWhite(user.getStarWhite() - slot.getRequiredStarCount());
                    break;
                case 2:
                    user.setStarBlue(user.getStarBlue() - slot.getRequiredStarCount());
                    break;
                case 3:
                    user.setStarRed(user.getStarRed() - slot.getRequiredStarCount());
                    break;
            }
            userRepository.save(user);

            // Lưu trạng thái khảm sao
            UserPetStar userPetStar;
            if (existingStar.isPresent()) {
                userPetStar = existingStar.get();
            } else {
                userPetStar = new UserPetStar();
                userPetStar.setUserId(request.getUserId());
                userPetStar.setPetId(request.getPetId());
                userPetStar.setImageIndex(slot.getImageIndex());
                userPetStar.setSlotId(request.getSlotId());
            }
            userPetStar.setInlaid(true);
            userPetStar.setInlaidAt(LocalDateTime.now());
            userPetStarRepository.save(userPetStar);

            // Kiểm tra xem đã khảm đủ sao chưa
            long totalSlots = petStarSlotRepository.countByPetId(request.getPetId());
            long inlaidSlots = userPetStarRepository
                    .countByUserIdAndPetIdAndInlaidTrue(request.getUserId(), request.getPetId());

            boolean petUnlocked = false;
            if (inlaidSlots >= totalSlots) {
                // Unlock pet cho user
                if (!userPetRepository.existsByUserIdAndPetId(request.getUserId(), request.getPetId())) {
                    UserPet userPet = new UserPet();
                    userPet.setUserId(request.getUserId());
                    userPet.setPetId(request.getPetId());
                    userPet.setLevel(14);
                    userPetRepository.save(userPet);
                    petUnlocked = true;
                }
            }

            response.setSuccess(true);
            response.setMessage("Star inlaid successfully");
            response.setRemainingWhiteStars(user.getStarWhite());
            response.setRemainingBlueStars(user.getStarBlue());
            response.setRemainingRedStars(user.getStarRed());
            response.setPetUnlocked(petUnlocked);

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error: " + e.getMessage());
        }

        return response;
    }

    /**
     * Lấy danh sách tất cả Pet Huyền Thoại
     */
    public List<Pet> getAllLegendPets() {
        return petRepository.findLegendPetsOrderByNoAsc();
    }

    @Transactional
    public UnlockPetResponse unlockLegendPet(UnlockPetRequest request) {
        UnlockPetResponse response = new UnlockPetResponse();
        try {
            Long userId = request.getUserId();
            Long petId = request.getPetId();

            // 1. Kiểm tra pet có tồn tại không
            Pet pet = petRepository.findById(petId)
                    .orElseThrow(() -> new RuntimeException("Pet không tồn tại"));

            if (!pet.isFlagLegend()) {
                response.setSuccess(false);
                response.setMessage("Pet này không phải là pet huyền thoại");
                return response;
            }

            // 2. Kiểm tra đã unlock chưa
            if (userPetRepository.existsByUserIdAndPetId(userId, petId)) {
                response.setSuccess(false);
                response.setMessage("Bạn đã sở hữu pet này rồi!");
                return response;
            }

            // 3. Kiểm tra đã khảm đủ sao chưa
            long totalSlots = petStarSlotRepository.countByPetId(petId);
            long inlaidSlots = userPetStarRepository
                    .countByUserIdAndPetIdAndInlaidTrue(userId, petId);

            if (inlaidSlots < totalSlots) {
                response.setSuccess(false);
                response.setMessage(String.format(
                        "Bạn chưa khảm đủ sao! Đã khảm: %d/%d",
                        inlaidSlots, totalSlots));
                return response;
            }

            // 4. Tạo UserPet
            UserPet userPet = new UserPet();
            userPet.setUserId(userId);
            userPet.setPetId(petId);
            userPet.setLevel(1);
            userPetRepository.save(userPet);

            // 5. Tạo response
            PetDataDTO petData = new PetDataDTO();
            petData.setPetId(pet.getId());
            petData.setName(pet.getName());
            petData.setUnlocked(true);
            petData.setLevel(1);

            response.setSuccess(true);
            response.setMessage("Chúc mừng! Bạn đã mở khóa " + pet.getName() + "!");
            response.setPetData(petData);


        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Lỗi: " + e.getMessage());
        }

        return response;
    }
}