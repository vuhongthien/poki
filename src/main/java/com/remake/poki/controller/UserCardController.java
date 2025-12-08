package com.remake.poki.controller;

import com.remake.poki.request.UseCardRequest;
import com.remake.poki.response.UseCardResponse;
import com.remake.poki.dto.CardDTO;
import com.remake.poki.service.UserCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-cards")
@RequiredArgsConstructor
public class UserCardController {

    private final UserCardService userCardService;

    /**
     * GET /api/user-cards/{userId}
     * Lấy danh sách card của user
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<CardDTO>> getUserCards(@PathVariable Long userId) {
        return ResponseEntity.ok(userCardService.getUserCards(userId));
    }

    /**
     * POST /api/user-cards/use
     * Sử dụng card
     * Body: { userId, cardId, quantity }
     */
    @PostMapping("/use")
    public ResponseEntity<UseCardResponse> useCard(@RequestBody UseCardRequest request) {
        return ResponseEntity.ok(userCardService.useCard(
            request.getUserId(),
            request.getCardId(),
            request.getQuantity()
        ));
    }
}
