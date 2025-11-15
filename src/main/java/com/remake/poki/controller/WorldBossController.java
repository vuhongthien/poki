package com.remake.poki.controller;

import com.remake.poki.dto.BossBattleResultDTO;
import com.remake.poki.dto.BossRankingDTO;
import com.remake.poki.dto.BossRewardDTO;
import com.remake.poki.dto.WorldBossDTO;
import com.remake.poki.service.WorldBossService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/world-boss")
public class WorldBossController {

    @Autowired
    private WorldBossService worldBossService;

    @GetMapping("/user/{userId}")
    public List<WorldBossDTO> getBossesForUser(@PathVariable Long userId) {
        return worldBossService.getAllBossesForUser(userId);
    }

    /**
     * Lấy danh sách tất cả boss với trạng thái cho user
     * GET /api/world-boss/all?userId=1
     */
    @GetMapping("/all")
    public ResponseEntity<List<WorldBossDTO>> getAllBosses(@RequestParam Long userId) {
        try {
            List<WorldBossDTO> bosses = worldBossService.getAllBossesForUser(userId);
            return ResponseEntity.ok(bosses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Sử dụng 1 lượt đánh boss
     * POST /api/world-boss/attempt/use?userId=1&bossScheduleId=1
     */
    @PostMapping("/attempt/use")
    public ResponseEntity<?> useBossAttempt(
            @RequestParam Long userId,
            @RequestParam Long bossScheduleId) {
        try {
            worldBossService.useBossAttempt(userId, bossScheduleId);
            return ResponseEntity.ok("Attempt used successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Gửi damage sau khi đánh boss
     * POST /api/world-boss/{bossScheduleId}/damage?userId=1
     * Body: BossBattleResultDTO
     */
    @PostMapping("/{bossScheduleId}/damage")
    public ResponseEntity<?> submitDamage(
            @PathVariable Long bossScheduleId,
            @RequestParam Long userId,
            @RequestBody BossBattleResultDTO result) {
        try {
            worldBossService.submitDamage(userId, bossScheduleId, result);
            return ResponseEntity.ok("Damage submitted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Lấy bảng xếp hạng Top 10
     * GET /api/world-boss/{bossScheduleId}/ranking
     */
    @GetMapping("/{bossScheduleId}/ranking")
    public ResponseEntity<List<BossRankingDTO>> getRanking(
            @PathVariable Long bossScheduleId) {
        try {
            List<BossRankingDTO> ranking = worldBossService.getTop10Ranking(bossScheduleId);
            return ResponseEntity.ok(ranking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Nhận thưởng sau khi boss kết thúc
     * POST /api/world-boss/{bossScheduleId}/reward?userId=1
     */
    @PostMapping("/{bossScheduleId}/reward")
    public ResponseEntity<?> claimReward(
            @PathVariable Long bossScheduleId,
            @RequestParam Long userId) {
        try {
            BossRewardDTO reward = worldBossService.claimReward(userId, bossScheduleId);
            return ResponseEntity.ok(reward);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}