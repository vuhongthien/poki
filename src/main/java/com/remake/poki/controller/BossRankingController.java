package com.remake.poki.controller;

import com.remake.poki.dto.BossRankingResponseDTO;
import com.remake.poki.dto.ClaimRewardResponseDTO;
import com.remake.poki.service.BossRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boss-ranking")
@RequiredArgsConstructor
public class BossRankingController {

    private final BossRankingService bossRankingService;

    /**
     * API: Lấy bảng xếp hạng boss vừa kết thúc
     * GET /api/boss-ranking/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<BossRankingResponseDTO> getBossRanking(@PathVariable Long userId) {
        BossRankingResponseDTO ranking = bossRankingService.getLatestBossRanking(userId);
        return ResponseEntity.ok(ranking);
    }

    /**
     * API: Nhận quà xếp hạng
     * POST /api/boss-ranking/claim-reward
     */
    @PostMapping("/claim-reward")
    public ResponseEntity<ClaimRewardResponseDTO> claimReward(
            @RequestParam Long userId,
            @RequestParam Long bossScheduleId) {

        ClaimRewardResponseDTO response = bossRankingService.claimReward(userId, bossScheduleId);
        return ResponseEntity.ok(response);
    }
}