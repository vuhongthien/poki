package com.remake.poki.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.remake.poki.dto.RechargeMilestoneDTO;
import com.remake.poki.dto.StoneRewardDTO;
import com.remake.poki.model.*;
import com.remake.poki.repo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MilestoneService {

    private final RechargeMilestoneRepository milestoneRepository;
    private final UserMilestoneRepository userMilestoneRepository;
    private final UserRechargeRepository userRechargeRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * Lấy tất cả mốc hỗ trợ với trạng thái của user
     */
    public List<RechargeMilestoneDTO> getAllMilestones(Long userId) {
        List<RechargeMilestone> milestones = milestoneRepository
                .findByIsActiveTrueOrderBySortOrder();

        Integer userTotalRecharge = userRechargeRepository
                .sumAmountByUserIdAndStatus(userId, "SUCCESS");

        if (userTotalRecharge == null) {
            userTotalRecharge = 0;
        }

        final Integer finalTotal = userTotalRecharge;

        return milestones.stream()
                .map(milestone -> convertToDTO(milestone, userId, finalTotal))
                .collect(Collectors.toList());
    }

    /**
     * Nhận quà mốc hỗ trợ
     */
    @Transactional
    public RechargeMilestoneDTO claimMilestone(Long milestoneId, Long userId) {
        // Validate milestone
        RechargeMilestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("Milestone not found"));

        if (!milestone.getIsActive()) {
            throw new RuntimeException("Milestone is not active");
        }

        // Check user total recharge
        Integer userTotalRecharge = userRechargeRepository
                .sumAmountByUserIdAndStatus(userId, "SUCCESS");

        if (userTotalRecharge == null) {
            userTotalRecharge = 0;
        }

        if (userTotalRecharge < milestone.getRequiredAmount()) {
            throw new RuntimeException("You haven't reached this milestone yet. Required: "
                    + milestone.getRequiredAmount() + ", Current: " + userTotalRecharge);
        }

        // Check if already claimed
        UserMilestone userMilestone = userMilestoneRepository
                .findByUserIdAndMilestoneId(userId, milestoneId)
                .orElse(null);

        if (userMilestone != null && userMilestone.getClaimed()) {
            throw new RuntimeException("You have already claimed this milestone");
        }

        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Add rewards to user
        if (milestone.getGold() != null) {
            user.setGold(user.getGold() + milestone.getGold());
        }
        if (milestone.getRuby() != null) {
            user.setRuby(user.getRuby() + milestone.getRuby());
        }
        if (milestone.getEnergy() != null) {
            user.setEnergy(user.getEnergy() + milestone.getEnergy());
        }
        if (milestone.getExp() != null) {
            user.setExp(user.getExp() + milestone.getExp());
        }
        if (milestone.getStarWhite() != null) {
            user.setStarWhite(user.getStarWhite() + milestone.getStarWhite());
        }
        if (milestone.getStarBlue() != null) {
            user.setStarBlue(user.getStarBlue() + milestone.getStarBlue());
        }
        if (milestone.getStarRed() != null) {
            user.setStarRed(user.getStarRed() + milestone.getStarRed());
        }
        if (milestone.getWheel() != null) {
            user.setWheel(user.getWheel() + milestone.getWheel());
        }
        // TODO: Handle pet, card, stones rewards

        userRepository.save(user);

        // Mark as claimed
        if (userMilestone == null) {
            userMilestone = new UserMilestone();
            userMilestone.setUserId(userId);
            userMilestone.setMilestoneId(milestoneId);
        }

        userMilestone.setClaimed(true);
        userMilestone.setClaimedAt(LocalDateTime.now());
        userMilestoneRepository.save(userMilestone);

        log.info("✅ User #{} claimed milestone #{}: {} gold",
                userId, milestoneId, milestone.getGold());

        return convertToDTO(milestone, userId, userTotalRecharge);
    }

    /**
     * Đếm số mốc có thể nhận
     */
    public Long countClaimableMilestones(Long userId) {
        Integer userTotalRecharge = userRechargeRepository
                .sumAmountByUserIdAndStatus(userId, "SUCCESS");

        if (userTotalRecharge == null) {
            userTotalRecharge = 0;
        }

        List<RechargeMilestone> milestones = milestoneRepository
                .findByIsActiveTrueOrderBySortOrder();

        final Integer finalTotal = userTotalRecharge;

        return milestones.stream()
                .filter(milestone -> {
                    if (milestone.getRequiredAmount() > finalTotal) {
                        return false;
                    }

                    UserMilestone userMilestone = userMilestoneRepository
                            .findByUserIdAndMilestoneId(userId, milestone.getId())
                            .orElse(null);

                    return userMilestone == null || !userMilestone.getClaimed();
                })
                .count();
    }

    // === PRIVATE METHODS ===

    private RechargeMilestoneDTO convertToDTO(RechargeMilestone milestone, Long userId, Integer userTotalRecharge) {
        List<StoneRewardDTO> stones = parseStones(milestone.getStonesJson());

        boolean canClaim = userTotalRecharge >= milestone.getRequiredAmount();

        UserMilestone userMilestone = userMilestoneRepository
                .findByUserIdAndMilestoneId(userId, milestone.getId())
                .orElse(null);

        boolean claimed = userMilestone != null && userMilestone.getClaimed();
        LocalDateTime claimedAt = userMilestone != null ? userMilestone.getClaimedAt() : null;

        return RechargeMilestoneDTO.builder()
                .id(milestone.getId())
                .name(milestone.getName())
                .description(milestone.getDescription())
                .requiredAmount(milestone.getRequiredAmount())
                .sortOrder(milestone.getSortOrder())
                .gold(milestone.getGold())
                .ruby(milestone.getRuby())
                .energy(milestone.getEnergy())
                .exp(milestone.getExp())
                .starWhite(milestone.getStarWhite())
                .starBlue(milestone.getStarBlue())
                .starRed(milestone.getStarRed())
                .wheel(milestone.getWheel())
                .petId(milestone.getPetId())
                .cardId(milestone.getCardId())
                .stones(stones)
                .iconUrl(milestone.getIconUrl())
                .isActive(milestone.getIsActive())
                .userTotalRecharge(userTotalRecharge)
                .canClaim(canClaim && !claimed)
                .claimed(claimed)
                .claimedAt(claimedAt)
                .createdAt(milestone.getCreatedAt())
                .updatedAt(milestone.getUpdatedAt())
                .build();
    }

    private List<StoneRewardDTO> parseStones(String stonesJson) {
        if (stonesJson == null || stonesJson.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(stonesJson, new TypeReference<List<StoneRewardDTO>>() {});
        } catch (Exception e) {
            log.error("Error parsing stones JSON: {}", stonesJson, e);
            return new ArrayList<>();
        }
    }
}