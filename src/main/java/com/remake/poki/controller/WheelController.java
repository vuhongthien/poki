package com.remake.poki.controller;

import com.remake.poki.dto.SpinCheckDTO;
import com.remake.poki.dto.SpinResultDTO;
import com.remake.poki.dto.WheelConfigDTO;
import com.remake.poki.service.WheelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wheel")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class WheelController {

    private final WheelService wheelService;

    /**
     * Lấy config vòng quay
     * GET /api/wheel/config/{userId}
     */
    @GetMapping("/config/{userId}")
    public ResponseEntity<WheelConfigDTO> getWheelConfig(@PathVariable Long userId) {
        log.info("GET /api/wheel/config/{}", userId);
        WheelConfigDTO config = wheelService.getWheelConfig(userId);
        return ResponseEntity.ok(config);
    }

    // ========================================
    // QUAY MIỄN PHÍ
    // ========================================

    /**
     * CHECK điều kiện trước khi quay miễn phí
     * GET /api/wheel/check-free/{userId}
     */
    @GetMapping("/check-free/{userId}")
    public ResponseEntity<SpinCheckDTO> checkFreeSpin(@PathVariable Long userId) {
        log.info("GET /api/wheel/check-free/{}", userId);

        try {
            SpinCheckDTO result = wheelService.checkFreeSpin(userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error checking free spin for user {}: {}", userId, e.getMessage());
            return ResponseEntity.ok(SpinCheckDTO.builder()
                    .canSpin(false)
                    .message("Lỗi: " + e.getMessage())
                    .build());
        }
    }

    /**
     * LƯU kết quả sau khi Unity quay xong (miễn phí)
     * POST /api/wheel/save-free/{userId}?prizeIndex=3
     */
    @PostMapping("/save-free/{userId}")
    public ResponseEntity<SpinResultDTO> saveFreeSpin(
            @PathVariable Long userId,
            @RequestParam int prizeIndex) {

        log.info("POST /api/wheel/save-free/{} prizeIndex={}", userId, prizeIndex);

        try {
            SpinResultDTO result = wheelService.saveFreeSpin(userId, prizeIndex);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error saving free spin for user {}: {}", userId, e.getMessage());
            return ResponseEntity.ok(SpinResultDTO.builder()
                    .success(false)
                    .message("Lỗi: " + e.getMessage())
                    .build());
        }
    }

    // ========================================
    // QUAY GOLD
    // ========================================

    /**
     * CHECK điều kiện trước khi quay Gold
     * GET /api/wheel/check-gold/{userId}
     */
    @GetMapping("/check-gold/{userId}")
    public ResponseEntity<SpinCheckDTO> checkGoldSpin(@PathVariable Long userId) {
        log.info("GET /api/wheel/check-gold/{}", userId);

        try {
            SpinCheckDTO result = wheelService.checkGoldSpin(userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error checking gold spin for user {}: {}", userId, e.getMessage());
            return ResponseEntity.ok(SpinCheckDTO.builder()
                    .canSpin(false)
                    .message("Lỗi: " + e.getMessage())
                    .build());
        }
    }

    /**
     * LƯU kết quả sau khi Unity quay xong (Gold)
     * POST /api/wheel/save-gold/{userId}?prizeIndex=5
     */
    @PostMapping("/save-gold/{userId}")
    public ResponseEntity<SpinResultDTO> saveGoldSpin(
            @PathVariable Long userId,
            @RequestParam int prizeIndex) {

        log.info("POST /api/wheel/save-gold/{} prizeIndex={}", userId, prizeIndex);

        try {
            SpinResultDTO result = wheelService.saveGoldSpin(userId, prizeIndex);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error saving gold spin for user {}: {}", userId, e.getMessage());
            return ResponseEntity.ok(SpinResultDTO.builder()
                    .success(false)
                    .message("Lỗi: " + e.getMessage())
                    .build());
        }
    }
}