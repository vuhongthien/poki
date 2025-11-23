package com.remake.poki.controller;

import com.remake.poki.dto.GiftDTO;
import com.remake.poki.request.CreateGiftRequest;
import com.remake.poki.service.GiftService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gifts")
@RequiredArgsConstructor
@Slf4j
public class GiftController {
    
    private final GiftService giftService;
    
    /**
     * GET /api/gifts/user/{userId}/pending
     * Lấy danh sách quà chưa nhận
     */
    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<List<GiftDTO>> getPendingGifts(@PathVariable Long userId) {
        log.info("→ GET pending gifts for user #{}", userId);
        List<GiftDTO> gifts = giftService.getPendingGifts(userId);
        return ResponseEntity.ok(gifts);
    }
    
    /**
     * GET /api/gifts/user/{userId}/count
     * Đếm số quà chưa nhận (để hiển thị badge)
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Long>> countPendingGifts(@PathVariable Long userId) {
        long count = giftService.countPendingGifts(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    /**
     * GET /api/gifts/user/{userId}/claimed
     * Lịch sử quà đã nhận
     */
    @GetMapping("/user/{userId}/claimed")
    public ResponseEntity<List<GiftDTO>> getClaimedGifts(@PathVariable Long userId) {
        log.info("→ GET claimed gifts for user #{}", userId);
        List<GiftDTO> gifts = giftService.getClaimedGifts(userId);
        return ResponseEntity.ok(gifts);
    }
    
    /**
     * POST /api/gifts/claim/{giftId}
     * Nhận quà
     */
    @PostMapping("/claim/{giftId}")
    public ResponseEntity<GiftDTO> claimGift(
            @PathVariable Long giftId,
            @RequestParam Long userId) {
        log.info("→ User #{} claiming gift #{}", userId, giftId);
        GiftDTO claimed = giftService.claimGift(giftId, userId);
        return ResponseEntity.ok(claimed);
    }
    
    // === ADMIN ENDPOINTS ===
    
    /**
     * POST /api/gifts/send/individual
     * Gửi quà cho 1 user cụ thể
     */
    @PostMapping("/send/individual")
    public ResponseEntity<GiftDTO> sendGiftToUser(@RequestBody CreateGiftRequest request) {
        log.info("→ Sending gift to user #{}: {}", request.getUserId(), request.getTitle());
        GiftDTO gift = giftService.sendGiftToUser(request);
        return ResponseEntity.ok(gift);
    }
    
    /**
     * POST /api/gifts/send/all
     * Gửi quà cho TẤT CẢ user
     */
    @PostMapping("/send/all")
    public ResponseEntity<GiftDTO> sendGiftToAllUsers(@RequestBody CreateGiftRequest request) {
        log.info("→ Sending gift to ALL USERS: {}", request.getTitle());
        GiftDTO gift = giftService.sendGiftToAllUsers(request);
        return ResponseEntity.ok(gift);
    }
}
