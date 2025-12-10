package com.remake.poki.service;

import com.remake.poki.dto.*;
import com.remake.poki.model.*;
import com.remake.poki.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorldBossService {

    @Autowired
    private UserBossDailyAttemptRepository attemptRepo;

    @Autowired
    private WorldBossDamageRepository damageRepo;

    @Autowired
    private WorldBossScheduleRepository scheduleRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PetRepository petRepo;
    @Autowired
    private UserRepository userRepository;

    /**
     * Lấy danh sách tất cả boss với thông tin trạng thái cho user
     */
    public List<WorldBossDTO> getAllBossesForUser(Long userId) {
        List<WorldBossSchedule> bosses = scheduleRepo.findByIsActiveTrueOrderByDisplayOrderAsc();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        return bosses.stream().map(boss -> {
            WorldBossDTO dto = new WorldBossDTO();
            dto.setId(boss.getId());
            dto.setPetId(boss.getPetId());
            dto.setBossName(boss.getBossName());
            dto.setBossLevel(boss.getBossLevel());
            dto.setBossHp(boss.getBossHp());
            dto.setBossAttack(boss.getBossAttack());
            dto.setBossMana(boss.getBossMana());

            // Lấy element type từ Pet
            petRepo.findById(boss.getPetId()).ifPresent(pet -> {
                dto.setElementType(String.valueOf(pet.getElementType()));
            });

            // Tính thời gian
            LocalDateTime startTime = LocalDateTime.of(today,
                    LocalTime.of(boss.getStartHour(), boss.getStartMinute()));
            LocalDateTime endTime = startTime.plusMinutes(boss.getDurationMinutes());

            dto.setStartTime(startTime.toString());
            dto.setEndTime(endTime.toString());

            // Xác định status
            LocalDateTime currentTime = LocalDateTime.now();
            if (currentTime.isBefore(startTime)) {
                dto.setStatus("UPCOMING");
            } else if (currentTime.isAfter(endTime)) {
                dto.setStatus("ENDED");
            } else {
                dto.setStatus("ACTIVE");
            }

            // Lấy số lượt còn lại
            Optional<UserBossDailyAttempt> attempt = attemptRepo
                    .findByUserIdAndBossScheduleIdAndAttemptDate(userId, boss.getId(), today);

            int usedAttempts = attempt.map(UserBossDailyAttempt::getAttemptCount).orElse(0);
            int maxAttempts = attempt.map(UserBossDailyAttempt::getMaxAttempts).orElse(3);

            dto.setRemainingAttempts(maxAttempts - usedAttempts);
            dto.setMaxAttempts(maxAttempts);

            // Lấy damage hiện tại của user
            Optional<WorldBossDamage> damage = damageRepo
                    .findByUserIdAndBossScheduleId(userId, boss.getId());

            dto.setCurrentDamage(damage.map(WorldBossDamage::getTotalDamage).orElse(0));

            // Tính rank (nếu có damage)
            if (damage.isPresent()) {
                long rank = damageRepo.countPlayersWithHigherDamage(
                        boss.getId(),
                        damage.get().getTotalDamage()
                ) + 1;
                dto.setUserRank((int) rank);
            } else {
                dto.setUserRank(0);
            }

            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Sử dụng 1 lượt đánh boss
     */
    @Transactional
    public void useBossAttempt(Long userId, Long bossScheduleId) {
        LocalDate today = LocalDate.now();

        // Kiểm tra boss có tồn tại không
        WorldBossSchedule boss = scheduleRepo.findById(bossScheduleId)
                .orElseThrow(() -> new RuntimeException("Boss không tồn tại!"));

        // Kiểm tra boss có đang ACTIVE không
        LocalDateTime startTime = LocalDateTime.of(today,
                LocalTime.of(boss.getStartHour(), boss.getStartMinute()));
        LocalDateTime endTime = startTime.plusMinutes(boss.getDurationMinutes());
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(startTime)) {
            throw new RuntimeException("Boss chưa bắt đầu!");
        }

        if (now.isAfter(endTime)) {
            throw new RuntimeException("Boss đã kết thúc!");
        }

        // Lấy hoặc tạo attempt record
        UserBossDailyAttempt attempt = attemptRepo
                .findByUserIdAndBossScheduleIdAndAttemptDate(userId, bossScheduleId, today)
                .orElse(new UserBossDailyAttempt());

        if (attempt.getId() == null) {
            attempt.setUserId(userId);
            attempt.setBossScheduleId(bossScheduleId);
            attempt.setAttemptDate(today);
            attempt.setAttemptCount(0);
            attempt.setMaxAttempts(3);
        }

        // Kiểm tra còn lượt không
        if (attempt.getAttemptCount() >= attempt.getMaxAttempts()) {
            throw new RuntimeException("Đã hết lượt đánh boss hôm nay!");
        }

        // Trừ 1 lượt
        attempt.setAttemptCount(attempt.getAttemptCount() + 1);
        attempt.setLastAttemptTime(LocalDateTime.now());

        attemptRepo.save(attempt);
    }

    /**
     * Gửi damage lên server
     */
    @Transactional
    public void submitDamage(Long userId, Long bossScheduleId, BossBattleResultDTO result) {
        // Kiểm tra boss có tồn tại không
        WorldBossSchedule boss = scheduleRepo.findById(bossScheduleId)
                .orElseThrow(() -> new RuntimeException("Boss không tồn tại!"));

        // Lấy hoặc tạo damage record
        WorldBossDamage damage = damageRepo
                .findByUserIdAndBossScheduleId(userId, bossScheduleId)
                .orElse(new WorldBossDamage());
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found!"));

        if (damage.getId() == null) {
            damage.setUserId(userId);
            damage.setUserPetId(user.getPetId());
            damage.setBossScheduleId(bossScheduleId);
            damage.setTotalDamage(0);
            damage.setBattleCount(0);
        }

        // Cộng damage
        damage.setTotalDamage(damage.getTotalDamage() + result.getDamageDealt());
        damage.setBattleCount(damage.getBattleCount() + 1);
        damage.setLastBattleTime(LocalDateTime.now());

        damageRepo.save(damage);
    }

    /**
     * Lấy Top 10 ranking
     */
    public List<BossRankingDTO> getTop10Ranking(Long bossScheduleId) {
        List<WorldBossDamage> topDamages = damageRepo
                .findTop10ByBossScheduleIdOrderByTotalDamageDesc(bossScheduleId);

        List<BossRankingDTO> rankings = new ArrayList<>();
        int rank = 1;

        for (WorldBossDamage damage : topDamages) {
            BossRankingDTO dto = new BossRankingDTO();
            dto.setUserPetId(damage.getUserPetId());
            dto.setUserId(damage.getUserId());
            dto.setTotalDamage(damage.getTotalDamage());
            dto.setRank(rank++);

            // Lấy tên user
            userRepo.findById(damage.getUserId()).ifPresent(user -> {
                dto.setUserName(user.getName());
            });

            rankings.add(dto);
        }

        return rankings;
    }

    /**
     * Tính và trả phần thưởng dựa trên rank
     */
    @Transactional
    public BossRewardDTO claimReward(Long userId, Long bossScheduleId) {
        // Kiểm tra boss
        WorldBossSchedule boss = scheduleRepo.findById(bossScheduleId)
                .orElseThrow(() -> new RuntimeException("Boss không tồn tại!"));

        // Kiểm tra đã kết thúc chưa
        LocalDate today = LocalDate.now();
        LocalDateTime endTime = LocalDateTime.of(today,
                        LocalTime.of(boss.getStartHour(), boss.getStartMinute()))
                .plusMinutes(boss.getDurationMinutes());

        if (LocalDateTime.now().isBefore(endTime)) {
            throw new RuntimeException("Boss chưa kết thúc!");
        }

        // Lấy damage record
        WorldBossDamage damage = damageRepo
                .findByUserIdAndBossScheduleId(userId, bossScheduleId)
                .orElseThrow(() -> new RuntimeException("Bạn chưa đánh boss này!"));

        // Kiểm tra đã nhận thưởng chưa
        if (damage.isRewardClaimed()) {
            throw new RuntimeException("Bạn đã nhận thưởng rồi!");
        }

        // Tính rank
        long rank = damageRepo.countPlayersWithHigherDamage(
                bossScheduleId,
                damage.getTotalDamage()
        ) + 1;

        // Tính phần thưởng theo rank
        BossRewardDTO reward = calculateReward(boss, (int) rank, damage.getTotalDamage());

        // Cộng phần thưởng cho user
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại!"));

        user.setGold(user.getGold() + reward.getGold());
        user.setExp(user.getExp() + reward.getExp());
        user.setStarWhite(user.getStarWhite() + reward.getStarWhite());
        user.setStarBlue(user.getStarBlue() + reward.getStarBlue());
        user.setStarRed(user.getStarRed() + reward.getStarRed());

        userRepo.save(user);

        // Đánh dấu đã nhận thưởng
        damage.setRewardClaimed(true);
        damage.setRewardClaimedTime(LocalDateTime.now());
        damageRepo.save(damage);

        return reward;
    }

    /**
     * Tính phần thưởng dựa trên rank
     */
    private BossRewardDTO calculateReward(WorldBossSchedule boss, int rank, int totalDamage) {
        BossRewardDTO reward = new BossRewardDTO();
        reward.setRank(rank);
        reward.setTotalDamage(totalDamage);

        // Phần thưởng theo rank
        if (rank == 1) {
            // Top 1: 200% phần thưởng
            reward.setGold((int) (boss.getRewardGold() * 2.0));
            reward.setExp((int) (boss.getRewardExp() * 2.0));
            reward.setStarWhite(boss.getRewardStarWhite() * 2);
            reward.setStarBlue(boss.getRewardStarBlue() * 2);
            reward.setStarRed(boss.getRewardStarRed() * 2);
        } else if (rank <= 3) {
            // Top 2-3: 150% phần thưởng
            reward.setGold((int) (boss.getRewardGold() * 1.5));
            reward.setExp((int) (boss.getRewardExp() * 1.5));
            reward.setStarWhite((int) (boss.getRewardStarWhite() * 1.5));
            reward.setStarBlue((int) (boss.getRewardStarBlue() * 1.5));
            reward.setStarRed((int) (boss.getRewardStarRed() * 1.5));
        } else if (rank <= 10) {
            // Top 4-10: 120% phần thưởng
            reward.setGold((int) (boss.getRewardGold() * 1.2));
            reward.setExp((int) (boss.getRewardExp() * 1.2));
            reward.setStarWhite((int) (boss.getRewardStarWhite() * 1.2));
            reward.setStarBlue((int) (boss.getRewardStarBlue() * 1.2));
            reward.setStarRed((int) (boss.getRewardStarRed() * 1.2));
        } else {
            // Ngoài top 10: 100% phần thưởng cơ bản
            reward.setGold(boss.getRewardGold());
            reward.setExp(boss.getRewardExp());
            reward.setStarWhite(boss.getRewardStarWhite());
            reward.setStarBlue(boss.getRewardStarBlue());
            reward.setStarRed(boss.getRewardStarRed());
        }

        return reward;
    }

    /**
     * Cleanup job - xóa attempt cũ hơn 7 ngày (tùy chọn)
     */
    @Transactional
    public void cleanupOldAttempts() {
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        attemptRepo.deleteByAttemptDateBefore(sevenDaysAgo);
    }
}