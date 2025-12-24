package com.remake.poki.service;

import com.remake.poki.enums.ElementType;
import com.remake.poki.model.*;
import com.remake.poki.repo.*;
import com.remake.poki.request.CreateGiftRequest;
import com.remake.poki.request.PetRequest;
import com.remake.poki.dto.StoneReward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private GiftService giftService;  // ✅ INJECT GiftService

    /**
     * ✅ Thêm Pet + CT + EXP (Unity đã tính)
     */
    @Transactional
    public void addPetToUser(Long userId, Long petId, int requestAttack, int expGain) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Cộng Request Attack
        user.setRequestAttack(user.getRequestAttack() + requestAttack);

        // ✅ Cộng EXP (Unity đã tính sẵn) + CHECK LEVEL UP
        addExpToUser(user, expGain);

        userRepository.save(user);

        System.out.println(String.format("[REWARD] User #%d - +%d CT, +%d EXP (Level %d: %d/%d)",
                userId, requestAttack, expGain, user.getLever(), user.getExpCurrent(), user.getExp()));

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
        } else {
            userPet.setPetId(petId);
        }
        userPetRepository.save(userPet);
    }

    /**
     * ✅ Chỉ cộng EXP (Unity đã tính) + CHECK LEVEL UP
     */
    @Transactional
    public void addExpToUserById(Long userId, int expGain) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        addExpToUser(user, expGain);
        userRepository.save(user);

        System.out.println(String.format("[REWARD] User #%d - +%d EXP (Level %d: %d/%d)",
                userId, expGain, user.getLever(), user.getExpCurrent(), user.getExp()));
    }

    /**
     * ✅ CORE: Xử lý EXP + Level Up + AUTO GIFT
     * Công thức: exp(level) = level * 100
     */
    public void addExpToUser(User user, int expGain) {
        if (expGain <= 0) return;

        user.setExpCurrent(user.getExpCurrent() + expGain);

        // ✅ Track các level đã lên để gửi gift
        List<Integer> leveledUpLevels = new ArrayList<>();

        // Check level up
        while (user.getExpCurrent() >= user.getExp()) {
            // Trừ EXP cũ
            user.setExpCurrent(user.getExpCurrent() - user.getExp());

            // Tăng level
            user.setLever(user.getLever() + 1);

            // ✅ TRACK LEVEL UP
            leveledUpLevels.add(user.getLever());

            // Tính EXP cần cho level mới: level * 100
            int newExpRequired = user.getLever() * 100;
            user.setExp(newExpRequired);

            System.out.println(String.format("[LEVEL UP] User #%d → Level %d (need %d EXP)",
                    user.getId(), user.getLever(), newExpRequired));
        }

        // ✅ GỬI GIFT CHO MỖI LEVEL ĐÃ LÊN
        for (Integer newLevel : leveledUpLevels) {
            sendLevelUpGift(user.getId(), newLevel);
        }
    }

    /**
     * ✅ GỬI GIFT KHI LEVEL UP
     * - 30 năng lượng
     * - 5 đá Lv7 mỗi loại (Fire, Water, Earth, Wind, Metal)
     * - Nếu level chia hết cho 10: thêm 20,000 gold
     */
    @Transactional
    public void sendLevelUpGift(Long userId, int newLevel) {
        try {
            // ✅ TẠO GIFT REQUEST
            CreateGiftRequest giftRequest = new CreateGiftRequest();
            giftRequest.setUserId(userId);
            giftRequest.setTitle("🎉 Phần thưởng Level " + newLevel);

            StringBuilder description = new StringBuilder();
            description.append("Chúc mừng bạn đã lên Level ").append(newLevel).append("!\n");
            description.append("Phần thưởng:\n");
            description.append("• 30 Năng lượng\n");
            description.append("• 3 Đá Lv6 mỗi loại");

            // ✅ NĂNG LƯỢNG: 30
            giftRequest.setEnergy(30);

            // ✅ ĐÁ LV7: 5 viên mỗi loại (Fire, Water, Earth, Wind, Metal)
            List<StoneReward> stones = new ArrayList<>();

            // Lấy stone IDs cho level 7 của mỗi hệ
            // Giả sử: Fire(1-7), Water(8-14), Earth(15-21), Wind(22-28), Metal(29-35)
            // → Level 7: Fire=7, Water=14, Earth=21, Wind=28, Metal=35

            stones.add(createStoneReward(6L, 3));    // Fire Lv7
            stones.add(createStoneReward(13L, 3));   // Water Lv7
            stones.add(createStoneReward(20L, 3));   // Earth Lv7
            stones.add(createStoneReward(27L, 3));   // Wind Lv7
            stones.add(createStoneReward(34L, 3));   // Metal Lv7

            giftRequest.setStones(stones);

            // ✅ NẾU LEVEL CHIA HẾT CHO 10: THÊM 20,000 GOLD
            if (newLevel % 10 == 0) {
                giftRequest.setGold(10000);
                description.append("\n• 10,000 Gold (Cột mốc Level ").append(newLevel).append(")");
            }

            giftRequest.setDescription(description.toString());

            // ✅ HẾT HẠN SAU 7 NGÀY
            giftRequest.setExpiredAt(LocalDateTime.now().plusDays(7));

            // ✅ GỬI GIFT
            giftService.sendGiftToUser(giftRequest);

            System.out.println(String.format("[LEVEL UP GIFT] Sent gift to user #%d for reaching level %d",
                    userId, newLevel));

        } catch (Exception e) {
            System.err.println(String.format("[LEVEL UP GIFT] Failed to send gift to user #%d: %s",
                    userId, e.getMessage()));
            e.printStackTrace();
        }
    }

    /**
     * ✅ MỚI: Chỉ cộng Gold
     */
    @Transactional
    public void addGoldToUserById(Long userId, int goldAmount) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        user.setGold(user.getGold() + goldAmount);
        userRepository.save(user);

        System.out.println(String.format("[BONUS] User #%d - +%d Gold 🍀", userId, goldAmount));
    }

    /**
     * ✅ HELPER: Tạo StoneReward
     */
    private StoneReward createStoneReward(Long stoneId, int count) {
        StoneReward reward = new StoneReward();
        reward.setStoneId(stoneId);
        reward.setCount(count);
        return reward;
    }

    /**
     * Thêm Stone cho User (KHÔNG có EXP)
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

        StoneUser existingStone = userStoneRepository
                .findByIdUserAndIdStone(userId, stone.getId())
                .orElse(null);

        if (existingStone != null) {
            existingStone.setCount(existingStone.getCount() + quantity);
            userStoneRepository.save(existingStone);
            System.out.println("Updated stone: userId=" + userId + ", stoneId=" + stone.getId() + ", newCount=" + existingStone.getCount());
        } else {
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
        } else {
            countPass.setCount(countPass.getCount() + 1);
        }
        countPassRepository.save(countPass);
    }
}