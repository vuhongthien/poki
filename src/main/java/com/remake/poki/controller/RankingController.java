package com.remake.poki.controller;

import com.remake.poki.dto.TopRankingDTO;
import com.remake.poki.dto.UserDetailDTO;
import com.remake.poki.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@CrossOrigin(origins = "*")
public class RankingController {
    
    @Autowired
    private RankingService rankingService;
    
    /**
     * API lấy top 9 người chơi có lực chiến cao nhất
     * GET /api/ranking/top9
     */
    @GetMapping("/top9")
    public ResponseEntity<List<TopRankingDTO>> getTop9Ranking() {
        List<TopRankingDTO> rankings = rankingService.getTop9Ranking();
        return ResponseEntity.ok(rankings);
    }
    
    /**
     * API lấy chi tiết thông tin user
     * GET /api/ranking/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserDetailDTO> getUserDetail(@PathVariable Long userId) {
        UserDetailDTO detail = rankingService.getUserDetail(userId);
        return ResponseEntity.ok(detail);
    }
}
