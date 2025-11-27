package com.remake.poki.controller;

import com.remake.poki.dto.*;
import com.remake.poki.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ShopController {

    private final ShopService shopService;

    /**
     * Lấy toàn bộ dữ liệu shop cho user
     * GET /api/shop/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ShopDataResponse> getShopData(@PathVariable Long userId) {
        try {
            ShopDataResponse response = shopService.getShopData(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Mua vật phẩm trong shop
     * POST /api/shop/purchase
     */
    @PostMapping("/purchase")
    public ResponseEntity<PurchaseResponse> purchaseItem(@RequestBody PurchaseRequest request) {
        try {
            PurchaseResponse response = shopService.purchaseItem(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new PurchaseResponse(false, "Có lỗi xảy ra: " + e.getMessage(), 0, 0));
        }
    }
}
