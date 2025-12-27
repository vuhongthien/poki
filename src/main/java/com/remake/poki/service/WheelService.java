package com.remake.poki.service;

import com.remake.poki.dto.*;
import com.remake.poki.model.*;
import com.remake.poki.repo.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WheelService {

    private final WheelPrizeConfigRepository prizeConfigRepository;
    private final WheelSpinHistoryRepository spinHistoryRepository;
    private final UserRepository userRepository;
    private final UserPetRepository userPetRepository;
    private final UserAvatarRepository userAvatarRepository;
    private final StoneUserRepository stoneUserRepository;
    private final StoneRepository stoneRepository;

    private static final int SPIN_COST = 10000;
    private static final int DUPLICATE_COMPENSATION = 100000;
    private final WheelSpinHistoryRepository wheelSpinHistoryRepository;

    private List<WheelPrizeConfig> cachedPrizes;

    @PostConstruct
    public void init() {
        refreshCache();
    }

    private void refreshCache() {
        cachedPrizes = prizeConfigRepository.findByActiveOrderBySlotIndexAsc(true);
        log.info("✓ Cached {} wheel prizes", cachedPrizes.size());
    }

    public WheelConfigDTO getWheelConfig(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        List<WheelPrizeDTO> prizeDTOs = new ArrayList<>();
        for (WheelPrizeConfig config : cachedPrizes) {
            prizeDTOs.add(convertToDTO(config));
        }

        WheelConfigDTO dto = new WheelConfigDTO();
        dto.setPrizes(prizeDTOs);
        dto.setSpinCost(SPIN_COST);
        dto.setUserGold(user.getGold());
        dto.setUserWheel(user.getWheelDay());

        return dto;
    }

    private WheelPrizeDTO convertToDTO(WheelPrizeConfig config) {
        return WheelPrizeDTO.builder()
                .index(config.getSlotIndex())
                .prizeType(config.getPrizeType().name())
                .prizeId(config.getPrizeId())
                .prizeName(config.getPrizeName())
                .amount(config.getAmount())
                .chance(config.getChance())
                .rarity(config.getRarity().name())
                .iconPath(config.getIconPath())
                .elementType(config.getElementType() != null ? config.getElementType().name() : null)
                .stoneLevel(config.getStoneLevel())
                .build();
    }

    // ========================================
    // QUAY MIỄN PHÍ
    // ========================================

    /**
     * CHECK điều kiện trước khi quay miễn phí
     */
    public SpinCheckDTO checkFreeSpin(Long userId) {
        log.info("→ Checking free spin for user {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        if (user.getWheelDay() <= 0) {
            return SpinCheckDTO.builder()
                    .canSpin(false)
                    .message("Không còn lượt quay miễn phí!")
                    .userGold(user.getGold())
                    .userWheel(0)
                    .build();
        }

        log.info("✓ User {} can spin free (remaining: {})", userId, user.getWheelDay());

        return SpinCheckDTO.builder()
                .canSpin(true)
                .message("OK")
                .userGold(user.getGold())
                .userWheel(user.getWheelDay())
                .build();
    }

    /**
     * LƯU kết quả sau khi Unity quay xong (miễn phí)
     */
    @Transactional
    public SpinResultDTO saveFreeSpin(Long userId, int prizeIndex) {
        log.info("→ Saving free spin result for user {}, prizeIndex={}", userId, prizeIndex);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Validate wheelDay (double check)
        if (user.getWheelDay() <= 0) {
            log.warn("✗ User {} has no free spins left!", userId);
            return SpinResultDTO.builder()
                    .success(false)
                    .message("Không còn lượt quay miễn phí!")
                    .remainingWheel(0)
                    .build();
        }

        // Validate prizeIndex
        WheelPrizeConfig prize = getPrizeByIndex(prizeIndex);
        if (prize == null) {
            log.error("✗ Invalid prizeIndex: {}", prizeIndex);
            return SpinResultDTO.builder()
                    .success(false)
                    .message("Prize index không hợp lệ!")
                    .build();
        }

        // Trừ 1 lượt miễn phí
        user.setWheelDay(user.getWheelDay() - 1);
        log.info("→ Deducted 1 free spin, remaining: {}", user.getWheelDay());

        // Grant reward và check duplicate
        boolean isDuplicate = grantRewardAndCheckDuplicate(user, prize);

        // Lưu lịch sử
        saveSpinHistory(userId, prize, 0, true);
        userRepository.save(user);

        log.info("✓ Free spin saved: prize={}, duplicate={}", prize.getPrizeName(), isDuplicate);

        return SpinResultDTO.builder()
                .success(true)
                .message("Lưu kết quả thành công!")
                .remainingGold(user.getGold())
                .remainingWheel(user.getWheelDay())
                .isDuplicate(isDuplicate)
                .compensationGold(isDuplicate ? DUPLICATE_COMPENSATION : 0)
                .build();
    }

    // ========================================
    // QUAY GOLD
    // ========================================

    /**
     * CHECK điều kiện trước khi quay Gold
     */
    public SpinCheckDTO checkGoldSpin(Long userId) {
        log.info("→ Checking gold spin for user {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        if (user.getGold() < SPIN_COST) {
            log.warn("✗ User {} has insufficient gold: {} < {}", userId, user.getGold(), SPIN_COST);
            return SpinCheckDTO.builder()
                    .canSpin(false)
                    .message("Không đủ Gold! Cần " + SPIN_COST + " Gold")
                    .userGold(user.getGold())
                    .userWheel(user.getWheelDay())
                    .build();
        }

        log.info("✓ User {} can spin with gold (current: {})", userId, user.getGold());

        return SpinCheckDTO.builder()
                .canSpin(true)
                .message("OK")
                .userGold(user.getGold())
                .userWheel(user.getWheelDay())
                .build();
    }

    /**
     * LƯU kết quả sau khi Unity quay xong (Gold)
     */
    @Transactional
    public SpinResultDTO saveGoldSpin(Long userId, int prizeIndex) {
        log.info("→ Saving gold spin result for user {}, prizeIndex={}", userId, prizeIndex);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Validate gold (double check)
        if (user.getGold() < SPIN_COST) {
            log.warn("✗ User {} has insufficient gold: {} < {}", userId, user.getGold(), SPIN_COST);
            return SpinResultDTO.builder()
                    .success(false)
                    .message("Không đủ Gold! Cần " + SPIN_COST + " Gold")
                    .remainingGold(user.getGold())
                    .build();
        }

        // Validate prizeIndex
        WheelPrizeConfig prize = getPrizeByIndex(prizeIndex);
        if (prize == null) {
            log.error("✗ Invalid prizeIndex: {}", prizeIndex);
            return SpinResultDTO.builder()
                    .success(false)
                    .message("Prize index không hợp lệ!")
                    .build();
        }

        // Trừ gold
        user.setGold(user.getGold() - SPIN_COST);
        log.info("→ Deducted {} gold, remaining: {}", SPIN_COST, user.getGold());

        // Grant reward và check duplicate
        boolean isDuplicate = grantRewardAndCheckDuplicate(user, prize);

        // Lưu lịch sử
        saveSpinHistory(userId, prize, SPIN_COST, false);
        userRepository.save(user);

        log.info("✓ Gold spin saved: prize={}, duplicate={}, remaining gold={}, remaining Energy={}",
                prize.getPrizeName(), isDuplicate, user.getGold(), user.getEnergy());

        return SpinResultDTO.builder()
                .success(true)
                .message("Lưu kết quả thành công!")
                .remainingGold(user.getGold())
                .remainingWheel(user.getWheelDay())
                .isDuplicate(isDuplicate)
                .compensationGold(isDuplicate ? DUPLICATE_COMPENSATION : 0)
                .build();
    }

    // ========================================
    // HELPERS
    // ========================================

    /**
     * Lấy prize theo index
     */
    private WheelPrizeConfig getPrizeByIndex(int index) {
        return cachedPrizes.stream()
                .filter(p -> p.getSlotIndex() == index)
                .findFirst()
                .orElse(null);
    }

    /**
     * Grant reward và trả về true nếu trùng Pet/Avatar
     */
    private boolean grantRewardAndCheckDuplicate(User user, WheelPrizeConfig prize) {
        switch (prize.getPrizeType()) {
            case PET:
                return grantPet(user, prize);
            case AVATAR:
                return grantAvatar(user, prize);
            case GOLD:
                user.setGold(user.getGold() + prize.getAmount());
                log.info("  → Granted {} gold", prize.getAmount());
                return false;
            case RUBY:
                user.setRuby(user.getRuby() + prize.getAmount());
                log.info("  → Granted {} ruby", prize.getAmount());
                return false;
            case ENERGY:
                user.setEnergy(user.getEnergy() + prize.getAmount());
                log.info("  → Granted {} energy", prize.getAmount());
                return false;
            case STONE:
                grantStone(user, prize);
                return false;
            default:
                return false;
        }
    }

    private boolean grantPet(User user, WheelPrizeConfig prize) {
        Optional<WheelSpinHistory> wheelSpinHistory = wheelSpinHistoryRepository.findByUserIdAndPrizeId(user.getId(), prize.getPrizeId());

        if (wheelSpinHistory.isEmpty()) {
            UserPet userPet = new UserPet();
            userPet.setUserId(user.getId());
            userPet.setPetId(prize.getPrizeId());
            userPet.setLevel(1);
            userPetRepository.save(userPet);
            log.info("  ✓ Granted NEW pet: {}", prize.getPrizeId());
            return false;
        } else {
            user.setGold(user.getGold() + DUPLICATE_COMPENSATION);
            log.info("  ✗ DUPLICATE pet {}, compensated {} gold",
                    prize.getPrizeId(), DUPLICATE_COMPENSATION);
            return true;
        }
    }

    private boolean grantAvatar(User user, WheelPrizeConfig prize) {
        Optional<UserAvatar> existing = userAvatarRepository.findByUserIdAndAvatarId(
                user.getId(), prize.getPrizeId());

        if (existing.isEmpty()) {
            UserAvatar userAvatar = new UserAvatar();
            userAvatar.setUserId(user.getId());
            userAvatar.setAvatarId(prize.getPrizeId());
            userAvatar.setCreated(LocalDateTime.now());
            userAvatarRepository.save(userAvatar);
            log.info("  ✓ Granted NEW avatar: {}", prize.getPrizeId());
            return false;
        } else {
            user.setGold(user.getGold() + DUPLICATE_COMPENSATION);
            log.info("  ✗ DUPLICATE avatar {}, compensated {} gold",
                    prize.getPrizeId(), DUPLICATE_COMPENSATION);
            return true;
        }
    }

    private void grantStone(User user, WheelPrizeConfig prize) {
        Optional<Stone> stoneOpt = stoneRepository.findByElementTypeAndLever(
                prize.getElementType(), prize.getStoneLevel());

        if (stoneOpt.isPresent()) {
            Stone stone = stoneOpt.get();
            Optional<StoneUser> existingStone = stoneUserRepository.findByIdUserAndIdStone(
                    user.getId(), stone.getId());

            if (existingStone.isPresent()) {
                StoneUser stoneUser = existingStone.get();
                stoneUser.setCount(stoneUser.getCount() + prize.getAmount());
                stoneUserRepository.save(stoneUser);
            } else {
                StoneUser stoneUser = new StoneUser();
                stoneUser.setIdUser(user.getId());
                stoneUser.setIdStone(stone.getId());
                stoneUser.setCount(prize.getAmount());
                stoneUserRepository.save(stoneUser);
            }
            log.info("  ✓ Granted {} stone {} Lv{}",
                    prize.getAmount(), prize.getElementType(), prize.getStoneLevel());
        }
    }

    private void saveSpinHistory(Long userId, WheelPrizeConfig prize, int goldSpent, boolean usedFreeTicket) {
        WheelSpinHistory history = new WheelSpinHistory();
        history.setUserId(userId);
        history.setPrizeType(prize.getPrizeType());
        history.setPrizeId(prize.getPrizeId());
        history.setPrizeName(prize.getPrizeName());
        history.setAmount(prize.getAmount());
        history.setGoldSpent(goldSpent);
        history.setUsedFreeTicket(usedFreeTicket);
        history.setSpinTime(LocalDateTime.now());
        history.setSlotIndex(prize.getSlotIndex());
        spinHistoryRepository.save(history);
        log.info("  ✓ Spin history saved");
    }
}