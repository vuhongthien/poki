package com.remake.poki.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.remake.poki.dto.GiftDTO;
import com.remake.poki.dto.StoneReward;
import com.remake.poki.enums.GiftStatus;
import com.remake.poki.enums.GiftType;
import com.remake.poki.model.*;
import com.remake.poki.repo.*;
import com.remake.poki.request.CreateGiftRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GiftService {

    private final GiftRepository giftRepository;
    private final UserGiftRepository userGiftRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final CardRepository cardRepository;
    private final StoneRepository stoneRepository;
    private final UserPetRepository userPetRepository;
    private final UserCardRepository userCardRepository;
    private final StoneUserRepository stoneUserRepository;

    /**
     * ✅ Gửi quà cho 1 user cụ thể
     * KHÔNG tạo UserGift - chờ user claim
     */
    @Transactional
    public GiftDTO sendGiftToUser(CreateGiftRequest request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required for individual gift");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));

        // Chỉ tạo Gift, KHÔNG tạo UserGift
        Gift gift = createGiftFromRequest(request);
        gift.setGiftType(GiftType.INDIVIDUAL);
        gift.setUserId(request.getUserId());
        gift.setStatus(GiftStatus.PENDING);
        Gift savedGift = giftRepository.save(gift);

        log.info("✓ Created gift #{} for user #{}: {}", savedGift.getId(), user.getId(), request.getTitle());

        return convertToDTO(savedGift);
    }

    /**
     * ✅ Gửi quà cho TẤT CẢ user
     * CHỈ tạo 1 Gift record - KHÔNG tạo UserGift
     */
    @Transactional
    public GiftDTO sendGiftToAllUsers(CreateGiftRequest request) {
        // CHỈ tạo Gift, KHÔNG tạo UserGift
        Gift gift = createGiftFromRequest(request);
        gift.setGiftType(GiftType.ALL_USERS);
        gift.setUserId(null); // null = tất cả
        gift.setStatus(GiftStatus.PENDING);
        Gift savedGift = giftRepository.save(gift);

        log.info("✓ Created gift #{} for ALL USERS: {}", savedGift.getId(), request.getTitle());
        log.info("   → UserGift will be created LAZILY when users claim");

        return convertToDTO(savedGift);
    }

    /**
     * ✅ Lấy danh sách quà chưa nhận của user
     * Logic: Tìm Gift + check UserGift
     */
    public List<GiftDTO> getPendingGifts(Long userId) {
        LocalDateTime now = LocalDateTime.now();

        // 1. Tìm tất cả Gifts còn hiệu lực mà user có thể nhận
        List<Gift> availableGifts = giftRepository.findAll().stream()
                .filter(gift -> gift.getStatus() == GiftStatus.PENDING)
                .filter(gift -> gift.getExpiredAt() == null || gift.getExpiredAt().isAfter(now))
                .filter(gift -> {
                    // INDIVIDUAL: Phải là của user này
                    if (gift.getGiftType() == GiftType.INDIVIDUAL) {
                        return gift.getUserId() != null && gift.getUserId().equals(userId);
                    }
                    // ALL_USERS: Tất cả đều được
                    return true;
                })
                .collect(Collectors.toList());

        if (availableGifts.isEmpty()) {
            return List.of();
        }

        // 2. Lọc bỏ gifts mà user ĐÃ CLAIM
        List<Long> giftIds = availableGifts.stream()
                .map(Gift::getId)
                .collect(Collectors.toList());

        // Tìm các gift đã claimed
        List<Long> claimedGiftIds = userGiftRepository.findAll().stream()
                .filter(ug -> ug.getUserId().equals(userId))
                .filter(ug -> giftIds.contains(ug.getGiftId()))
                .filter(ug -> ug.getStatus() == GiftStatus.CLAIMED)
                .map(UserGift::getGiftId)
                .collect(Collectors.toList());

        // 3. Trả về gifts chưa claimed
        return availableGifts.stream()
                .filter(gift -> !claimedGiftIds.contains(gift.getId()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * ✅ Đếm số quà chưa nhận
     */
    public long countPendingGifts(Long userId) {
        return getPendingGifts(userId).size();
    }

    /**
     * ✅ Nhận quà - TẠO UserGift lúc này
     */
    @Transactional
    public GiftDTO claimGift(Long giftId, Long userId) {
        // 1. Tìm Gift
        Gift gift = giftRepository.findById(giftId)
                .orElseThrow(() -> new IllegalArgumentException("Gift not found: " + giftId));

        // 2. Validate Gift
        if (gift.getStatus() != GiftStatus.PENDING) {
            throw new IllegalStateException("Gift is no longer available");
        }

        if (gift.getExpiredAt() != null && gift.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Gift has expired");
        }

        // 3. Validate quyền nhận
        if (gift.getGiftType() == GiftType.INDIVIDUAL) {
            if (gift.getUserId() == null || !gift.getUserId().equals(userId)) {
                throw new IllegalArgumentException("This gift is not for you");
            }
        }

        // 4. ✅ LAZY: Kiểm tra UserGift - nếu chưa có thì tạo
        UserGift userGift = userGiftRepository.findByUserIdAndGiftId(userId, giftId)
                .orElse(null);

        if (userGift != null && userGift.getStatus() == GiftStatus.CLAIMED) {
            throw new IllegalStateException("You already claimed this gift");
        }

        // 5. Lấy User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // 6. Thêm rewards vào user
        applyRewards(gift, user);
        userRepository.save(user);

        // 7. ✅ TẠO hoặc UPDATE UserGift (LAZY)
        if (userGift == null) {
            // Tạo mới
            userGift = new UserGift();
            userGift.setUserId(userId);
            userGift.setGiftId(giftId);
            userGift.setStatus(GiftStatus.CLAIMED);
            userGift.setClaimedAt(LocalDateTime.now());
            log.info("✓ Created NEW UserGift for user #{} gift #{}", userId, giftId);
        } else {
            // Update existing
            userGift.setStatus(GiftStatus.CLAIMED);
            userGift.setClaimedAt(LocalDateTime.now());
            log.info("✓ Updated UserGift for user #{} gift #{}", userId, giftId);
        }

        userGiftRepository.save(userGift);

        // 8. Nếu là INDIVIDUAL gift → update Gift status
        if (gift.getGiftType() == GiftType.INDIVIDUAL) {
            gift.setStatus(GiftStatus.CLAIMED);
            gift.setClaimedAt(LocalDateTime.now());
            giftRepository.save(gift);
            log.info("✓ Marked INDIVIDUAL gift #{} as CLAIMED", giftId);
        }

        log.info("✓ User #{} claimed gift #{}: {}", userId, giftId, gift.getTitle());

        return convertToDTO(gift);
    }

    /**
     * ✅ Áp dụng rewards vào user
     */
    private void applyRewards(Gift gift, User user) {
        // Basic rewards
        if (gift.getGold() != null && gift.getGold() > 0) {
            user.setGold(user.getGold() + gift.getGold());
        }

        if (gift.getEnergy() != null && gift.getEnergy() > 0) {
            int newEnergy = Math.min(user.getEnergy() + gift.getEnergy(), user.getEnergyFull());
            user.setEnergy(newEnergy);
        }

        if (gift.getExp() != null && gift.getExp() > 0) {
            user.setExpCurrent(user.getExpCurrent() + gift.getExp());
        }

        if (gift.getStarWhite() != null && gift.getStarWhite() > 0) {
            user.setStarWhite(user.getStarWhite() + gift.getStarWhite());
        }

        if (gift.getStarBlue() != null && gift.getStarBlue() > 0) {
            user.setStarBlue(user.getStarBlue() + gift.getStarBlue());
        }

        if (gift.getStarRed() != null && gift.getStarRed() > 0) {
            user.setStarRed(user.getStarRed() + gift.getStarRed());
        }

        if (gift.getWheel() != null && gift.getWheel() > 0) {
            user.setWheel(user.getWheel() + gift.getWheel());
        }

        // Complex rewards
        if (gift.getPetId() != null) {
            UserPet userPet = new UserPet();
            userPet.setUserId(user.getId());
            userPet.setPetId(gift.getPetId());
            userPet.setLevel(1);
            userPetRepository.save(userPet);
            log.info("✓ Added pet #{} to user #{}", gift.getPetId(), user.getId());
        }

        if (gift.getCardId() != null) {
            UserCard userCard = new UserCard();
            userCard.setUserId(user.getId());
            userCard.setCardId(gift.getCardId());
            userCard.setLevel(1);
            userCardRepository.save(userCard);
            log.info("✓ Added card #{} to user #{}", gift.getCardId(), user.getId());
        }

        // Multiple stones
        if (gift.getStonesJson() != null && !gift.getStonesJson().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<StoneReward> stones = mapper.readValue(
                        gift.getStonesJson(),
                        new TypeReference<List<StoneReward>>() {}
                );

                for (StoneReward stoneReward : stones) {
                    StoneUser stoneUser = stoneUserRepository
                            .findByIdUserAndIdStone(user.getId(), stoneReward.getStoneId())
                            .orElse(new StoneUser());

                    stoneUser.setIdUser(user.getId());
                    stoneUser.setIdStone(stoneReward.getStoneId());
                    stoneUser.setCount(stoneUser.getCount() + stoneReward.getCount());
                    stoneUserRepository.save(stoneUser);

                    log.info("✓ Added {} stones (ID: {}) to user #{}",
                            stoneReward.getCount(), stoneReward.getStoneId(), user.getId());
                }
            } catch (Exception e) {
                log.error("Error parsing stones JSON", e);
            }
        }
    }

    /**
     * ✅ Lịch sử quà đã nhận
     */
    public List<GiftDTO> getClaimedGifts(Long userId) {
        // Tìm UserGifts đã claimed
        List<UserGift> claimedUserGifts = userGiftRepository
                .findByUserIdAndStatusOrderByClaimedAtDesc(userId, GiftStatus.CLAIMED);

        if (claimedUserGifts.isEmpty()) {
            return List.of();
        }

        // Lấy Gift tương ứng
        List<Long> giftIds = claimedUserGifts.stream()
                .map(UserGift::getGiftId)
                .collect(Collectors.toList());

        List<Gift> gifts = giftRepository.findAllById(giftIds);

        return gifts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * ✅ Cron job: Tự động expire quà hết hạn
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    @Transactional
    public void expireOldGifts() {
        LocalDateTime now = LocalDateTime.now();

        // Tìm gifts hết hạn
        List<Gift> expiredGifts = giftRepository.findExpiredGifts(now);

        for (Gift gift : expiredGifts) {
            gift.setStatus(GiftStatus.EXPIRED);
        }

        if (!expiredGifts.isEmpty()) {
            giftRepository.saveAll(expiredGifts);
            log.info("✓ Expired {} gifts", expiredGifts.size());
        }

        // Không cần update UserGift vì chúng ta check gift.status khi getPendingGifts()
    }

    // ========================================
    // HELPER METHODS
    // ========================================

    private Gift createGiftFromRequest(CreateGiftRequest request) {
        Gift gift = new Gift();
        gift.setTitle(request.getTitle());
        gift.setDescription(request.getDescription());
        gift.setGold(request.getGold());
        gift.setEnergy(request.getEnergy());
        gift.setExp(request.getExp());
        gift.setStarWhite(request.getStarWhite());
        gift.setStarBlue(request.getStarBlue());
        gift.setStarRed(request.getStarRed());
        gift.setWheel(request.getWheel());
        gift.setPetId(request.getPetId());
        gift.setCardId(request.getCardId());

        // Convert stones list to JSON
        if (request.getStones() != null && !request.getStones().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String stonesJson = mapper.writeValueAsString(request.getStones());
                gift.setStonesJson(stonesJson);
            } catch (Exception e) {
                log.error("Error converting stones to JSON", e);
            }
        }

        gift.setExpiredAt(request.getExpiredAt());
        return gift;
    }

    private GiftDTO convertToDTO(Gift gift) {
        GiftDTO dto = GiftDTO.builder()
                .id(gift.getId())
                .userId(gift.getUserId())
                .title(gift.getTitle())
                .description(gift.getDescription())
                .giftType(gift.getGiftType())
                .status(gift.getStatus())
                .gold(gift.getGold())
                .energy(gift.getEnergy())
                .exp(gift.getExp())
                .starWhite(gift.getStarWhite())
                .starBlue(gift.getStarBlue())
                .starRed(gift.getStarRed())
                .wheel(gift.getWheel())
                .petId(gift.getPetId())
                .cardId(gift.getCardId())
                .createdAt(gift.getCreatedAt())
                .expiredAt(gift.getExpiredAt())
                .claimedAt(gift.getClaimedAt())
                .build();

        // Load pet name
        if (gift.getPetId() != null) {
            petRepository.findById(gift.getPetId())
                    .ifPresent(pet -> dto.setPetName(pet.getName()));
        }

        // Load card name
        if (gift.getCardId() != null) {
            cardRepository.findById(gift.getCardId())
                    .ifPresent(card -> dto.setCardName(card.getName()));
        }

        // Parse and enrich stones
        dto.setStones(parseAndEnrichStones(gift.getStonesJson()));

        return dto;
    }

    private List<StoneReward> parseAndEnrichStones(String stonesJson) {
        if (stonesJson == null || stonesJson.isEmpty()) {
            return List.of();
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<StoneReward> stones = mapper.readValue(
                    stonesJson,
                    new TypeReference<List<StoneReward>>() {}
            );

            if (stones.isEmpty()) {
                return stones;
            }

            // Batch load all stones at once
            List<Long> stoneIds = stones.stream()
                    .map(StoneReward::getStoneId)
                    .distinct()
                    .toList();

            Map<Long, Stone> stoneMap = stoneRepository.findAllById(stoneIds)
                    .stream()
                    .collect(Collectors.toMap(Stone::getId, Function.identity()));

            // Enrich each stone reward with details
            stones.forEach(stoneReward -> {
                Stone stone = stoneMap.get(stoneReward.getStoneId());
                if (stone != null) {
                    stoneReward.setStoneName(stone.getName());
                    stoneReward.setElementType(stone.getElementType().name());
                    stoneReward.setLevel(stone.getLever());
                }
            });

            return stones;

        } catch (Exception e) {
            log.error("Error parsing stones JSON: {}", stonesJson, e);
            return List.of();
        }
    }
}