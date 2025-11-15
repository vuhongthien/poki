package com.remake.poki.controller;

import com.remake.poki.dto.InlayStarRequest;
import com.remake.poki.dto.InlayStarResponse;
import com.remake.poki.dto.LegendPetDTO;
import com.remake.poki.model.Pet;
import com.remake.poki.request.UnlockPetRequest;
import com.remake.poki.response.UnlockPetResponse;
import com.remake.poki.service.LegendPetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/legend-pets")
@CrossOrigin(origins = "*")
public class LegendPetController {

    @Autowired
    private LegendPetService legendPetService;

    /**
     * Lấy danh sách tất cả Pet Huyền Thoại
     */
    @GetMapping
    public ResponseEntity<List<Pet>> getAllLegendPets() {
        return ResponseEntity.ok(legendPetService.getAllLegendPets());
    }

    /**
     * Lấy thông tin chi tiết Pet Huyền Thoại của user
     */
    @GetMapping("/{petId}/user/{userId}")
    public ResponseEntity<LegendPetDTO> getLegendPetInfo(
            @PathVariable Long petId,
            @PathVariable Long userId) {
        try {
            LegendPetDTO dto = legendPetService.getLegendPetInfo(userId, petId);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Khảm sao vào slot
     */
    @PostMapping("/inlay")
    public ResponseEntity<InlayStarResponse> inlayStar(@RequestBody InlayStarRequest request) {
        InlayStarResponse response = legendPetService.inlayStar(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * POST /api/legend-pets/unlock
     * Mở khóa Pet Huyền Thoại (Manual - optional)
     * Body: { userId, petId }
     */
    @PostMapping("/unlock")
    public ResponseEntity<UnlockPetResponse> unlockLegendPet(@RequestBody UnlockPetRequest request) {

        try {
            UnlockPetResponse response = legendPetService.unlockLegendPet(request);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            UnlockPetResponse errorResponse = new UnlockPetResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Lỗi server: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}