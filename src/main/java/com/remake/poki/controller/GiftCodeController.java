package com.remake.poki.controller;

import com.remake.poki.service.GiftCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/giftcode")
public class GiftCodeController {

    @Autowired
    private GiftCodeService giftCodeService;

    @PostMapping("/redeem")
    public ResponseEntity<?> redeemGiftCode(
            @RequestParam Long userId,
            @RequestParam String code
    ) {
        try {
            String message = giftCodeService.redeemGiftCode(userId, code);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", message);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
}