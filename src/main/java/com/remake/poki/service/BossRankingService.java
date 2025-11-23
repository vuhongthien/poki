package com.remake.poki.service;

import com.remake.poki.dto.BossRankingPlayerDTO;
import com.remake.poki.dto.BossRankingResponseDTO;
import com.remake.poki.dto.ClaimRewardResponseDTO;
import com.remake.poki.dto.RewardDetailDTO;
import com.remake.poki.model.User;
import com.remake.poki.model.WorldBossDamage;
import com.remake.poki.model.WorldBossSchedule;
import com.remake.poki.repo.UserRepository;
import com.remake.poki.repo.WorldBossDamageRepository;
import com.remake.poki.repo.WorldBossScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BossRankingService {

    private final WorldBossScheduleRepository bossScheduleRepository;
    private final WorldBossDamageRepository bossDamageRepository;
    private final UserRepository userRepository;

    /**
     * Lấy bảng xếp hạng của boss ĐÃ KẾT THÚC gần nhất
     */
    @Transactional(readOnly = true)
    public BossRankingResponseDTO getLatestBossRanking(Long currentUserId) {
        // 1. Lấy thông tin user hiện tại (BẮT BUỘC)
        User currentUser = userRepository.findById(currentUserId).orElse(null);
        if (currentUser == null) {
            throw new RuntimeException("User not found with id: " + currentUserId);
        }

        // 2. Tìm boss ĐÃ KẾT THÚC gần nhất
        WorldBossSchedule latestFinishedBoss = findLatestFinishedBoss();

        // 3. Nếu không có boss nào đã kết thúc
        if (latestFinishedBoss == null) {
            return BossRankingResponseDTO.builder()
                    .bossScheduleId(0L)
                    .bossName("Chưa có boss nào kết thúc")
                    .topPlayers(new ArrayList<>())
                    .currentPlayer(createEmptyPlayerDTO(currentUser, 0L))
                    .build();
        }

        // 4. Lấy danh sách damage của boss này
        List<WorldBossDamage> allDamages = bossDamageRepository
                .findTopPlayersByBossScheduleId(latestFinishedBoss.getId());

        // 5. Tạo top 10
        List<BossRankingPlayerDTO> topPlayers = new ArrayList<>();
        int rank = 1;

        for (int i = 0; i < Math.min(10, allDamages.size()); i++) {
            WorldBossDamage damage = allDamages.get(i);
            User user = userRepository.findById(damage.getUserId()).orElse(null);

            if (user != null) {
                BossRankingPlayerDTO playerDTO = BossRankingPlayerDTO.builder()
                        .userId(user.getId())
                        .userName(user.getName())
                        .petId(user.getPetId() != null ? user.getPetId() : 0L)
                        .bossId(latestFinishedBoss.getPetId() != null ? latestFinishedBoss.getPetId() : 0L)
                        .totalDamage(damage.getTotalDamage())
                        .rank(rank++)
                        .canClaimReward(true)
                        .rewardClaimed(damage.isRewardClaimed())
                        .build();

                topPlayers.add(playerDTO);
            }
        }

        // 6. Tìm thông tin của người chơi hiện tại
        BossRankingPlayerDTO currentPlayer = null;
        Optional<WorldBossDamage> currentUserDamage = bossDamageRepository
                .findByUserIdAndBossScheduleId(currentUserId, latestFinishedBoss.getId());

        Long bossId = latestFinishedBoss.getPetId() != null ? latestFinishedBoss.getPetId() : 0L;

        if (currentUserDamage.isPresent()) {
            // User có tham gia đánh boss này
            WorldBossDamage damage = currentUserDamage.get();
            int currentRank = calculateRank(allDamages, currentUserId);

            currentPlayer = BossRankingPlayerDTO.builder()
                    .userId(currentUser.getId())
                    .userName(currentUser.getName())
                    .petId(currentUser.getPetId() != null ? currentUser.getPetId() : 0L)
                    .bossId(bossId)
                    .totalDamage(damage.getTotalDamage())
                    .rank(currentRank)
                    .canClaimReward(currentRank <= 10)
                    .rewardClaimed(damage.isRewardClaimed())
                    .build();
        } else {
            // User KHÔNG tham gia đánh boss này
            currentPlayer = BossRankingPlayerDTO.builder()
                    .userId(currentUser.getId())
                    .userName(currentUser.getName())
                    .petId(currentUser.getPetId() != null ? currentUser.getPetId() : 0L)
                    .bossId(bossId)
                    .totalDamage(0)
                    .rank(0)
                    .canClaimReward(false)
                    .rewardClaimed(false)
                    .build();
        }

        // 7. Trả về kết quả
        return BossRankingResponseDTO.builder()
                .bossScheduleId(latestFinishedBoss.getId())
                .bossName(latestFinishedBoss.getBossName() != null ? latestFinishedBoss.getBossName() : "Unknown Boss")
                .topPlayers(topPlayers)
                .currentPlayer(currentPlayer)
                .build();
    }

    /**
     * Tạo DTO rỗng cho user chưa tham gia
     */
    private BossRankingPlayerDTO createEmptyPlayerDTO(User user, Long bossId) {
        return BossRankingPlayerDTO.builder()
                .userId(user.getId())
                .userName(user.getName())
                .petId(user.getPetId() != null ? user.getPetId() : 0L)
                .bossId(bossId)
                .totalDamage(0)
                .rank(0)
                .canClaimReward(false)
                .rewardClaimed(false)
                .build();
    }

    /**
     * Tìm boss ĐÃ KẾT THÚC gần nhất với thời gian hiện tại của server
     *
     * Logic:
     * 1. Lấy tất cả boss active
     * 2. Tính thời gian kết thúc của mỗi boss trong vòng 7 ngày gần đây
     * 3. Lọc ra những boss đã kết thúc (endTime < now)
     * 4. Sắp xếp theo thời gian kết thúc giảm dần
     * 5. Lấy boss đầu tiên (boss vừa kết thúc gần nhất)
     */
    private WorldBossSchedule findLatestFinishedBoss() {
        List<WorldBossSchedule> allBosses = bossScheduleRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        // Danh sách các boss đã kết thúc kèm thời gian kết thúc
        class BossWithEndTime {
            WorldBossSchedule boss;
            LocalDateTime endTime;

            BossWithEndTime(WorldBossSchedule boss, LocalDateTime endTime) {
                this.boss = boss;
                this.endTime = endTime;
            }
        }

        List<BossWithEndTime> finishedBosses = new ArrayList<>();

        for (WorldBossSchedule boss : allBosses) {
            if (!boss.isActive()) {
                continue;
            }

            LocalTime startTime = LocalTime.of(boss.getStartHour(), boss.getStartMinute());

            // Kiểm tra boss trong vòng 7 ngày gần đây
            for (int daysAgo = 0; daysAgo <= 7; daysAgo++) {
                LocalDate date = LocalDate.now().minusDays(daysAgo);
                LocalDateTime bossStart = date.atTime(startTime);
                LocalDateTime bossEnd = bossStart.plusMinutes(boss.getDurationMinutes());

                // Nếu boss đã kết thúc
                if (bossEnd.isBefore(now)) {
                    finishedBosses.add(new BossWithEndTime(boss, bossEnd));
                    break; // Chỉ lấy lần xuất hiện gần nhất của boss này
                }
            }
        }

        // Nếu không có boss nào kết thúc
        if (finishedBosses.isEmpty()) {
            return null;
        }

        // Sắp xếp theo thời gian kết thúc giảm dần (boss kết thúc gần nhất lên đầu)
        finishedBosses.sort(Comparator.comparing((BossWithEndTime b) -> b.endTime).reversed());

        WorldBossSchedule result = finishedBosses.get(0).boss;

        System.out.println("[BossRankingService] Latest finished boss: " + result.getBossName()
                + " (ended at: " + finishedBosses.get(0).endTime + ")");

        return result;
    }

    /**
     * Tính rank của một user cụ thể
     */
    private int calculateRank(List<WorldBossDamage> allDamages, Long userId) {
        for (int i = 0; i < allDamages.size(); i++) {
            if (allDamages.get(i).getUserId().equals(userId)) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * Nhận quà
     */
    @Transactional
    public ClaimRewardResponseDTO claimReward(Long userId, Long bossScheduleId) {
        Optional<WorldBossDamage> damageOpt = bossDamageRepository
                .findByUserIdAndBossScheduleId(userId, bossScheduleId);

        if (damageOpt.isEmpty()) {
            return ClaimRewardResponseDTO.builder()
                    .success(false)
                    .message("Bạn chưa tham gia boss này!")
                    .build();
        }

        WorldBossDamage damage = damageOpt.get();

        // Kiểm tra đã nhận quà chưa
        if (damage.isRewardClaimed()) {
            return ClaimRewardResponseDTO.builder()
                    .success(false)
                    .message("Bạn đã nhận quà rồi!")
                    .build();
        }

        // Kiểm tra có trong top 10 không
        List<WorldBossDamage> allDamages = bossDamageRepository
                .findTopPlayersByBossScheduleId(bossScheduleId);

        int rank = calculateRank(allDamages, userId);

        if (rank <= 0 || rank > 10) {
            return ClaimRewardResponseDTO.builder()
                    .success(false)
                    .message("Bạn không nằm trong top 10!")
                    .build();
        }

        // Tặng quà dựa theo rank
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ClaimRewardResponseDTO.builder()
                    .success(false)
                    .message("Không tìm thấy thông tin người chơi!")
                    .build();
        }

        int rewardWheel = 0;
        int rewardGold = 0;
        int rewardEnergy = 0;

        switch (rank) {
            case 1:
                // Top 1: 3 vòng quay + 20000 gold
                rewardWheel = 3;
                rewardGold = 20000;
                user.setWheel(user.getWheel() + rewardWheel);
                user.setGold(user.getGold() + rewardGold);
                System.out.println("[BossRankingService] Top 1 reward: +3 wheel, +20000 gold");
                break;

            case 2:
                // Top 2: 2 vòng quay
                rewardWheel = 2;
                user.setWheel(user.getWheel() + rewardWheel);
                System.out.println("[BossRankingService] Top 2 reward: +2 wheel");
                break;

            case 3:
                // Top 3: 1 vòng quay
                rewardWheel = 1;
                user.setWheel(user.getWheel() + rewardWheel);
                System.out.println("[BossRankingService] Top 3 reward: +1 wheel");
                break;

            default:
                // Top 4-10: 80 năng lượng
                rewardEnergy = 80;
                user.setEnergy(user.getEnergy() + rewardEnergy);
                System.out.println("[BossRankingService] Top " + rank + " reward: +80 energy");
                break;
        }

        userRepository.save(user);

        // Đánh dấu đã nhận quà
        damage.setRewardClaimed(true);
        damage.setRewardClaimedTime(LocalDateTime.now());
        bossDamageRepository.save(damage);

        // Tạo message chi tiết
        StringBuilder message = new StringBuilder("Chúc mừng bạn đạt hạng " + rank + "!\n\n");
        message.append("Phần thưởng:\n");

        if (rewardWheel > 0) {
            message.append("• ").append(rewardWheel).append(" Vòng quay\n");
        }
        if (rewardGold > 0) {
            message.append("• ").append(rewardGold).append(" Gold\n");
        }
        if (rewardEnergy > 0) {
            message.append("• ").append(rewardEnergy).append(" Năng lượng\n");
        }

        return ClaimRewardResponseDTO.builder()
                .success(true)
                .message(message.toString())
                .reward(RewardDetailDTO.builder()
                        .rank(rank)
                        .wheel(rewardWheel)
                        .gold(rewardGold)
                        .energy(rewardEnergy)
                        .build())
                .build();
    }
}