package com.remake.poki.controller;

import com.remake.poki.dto.AvatarEquipmentDTO;
import com.remake.poki.dto.PetEquipmentDTO;
import com.remake.poki.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    /**
     * GET /api/equipment/{userId}/pets
     * Lấy danh sách tất cả pet của user (tối đa 10 pets/page)
     */
    @GetMapping("/{userId}/pets")
    public ResponseEntity<List<PetEquipmentDTO>> getUserPets(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<PetEquipmentDTO> pets = equipmentService.getUserPets(userId, page, size);
        return ResponseEntity.ok(pets);
    }

    /**
     * GET /api/equipment/{userId}/avatars
     * Lấy danh sách tất cả avatar của user (tối đa 3 avatars/page)
     */
    @GetMapping("/{userId}/avatars")
    public ResponseEntity<List<AvatarEquipmentDTO>> getUserAvatars(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {

        List<AvatarEquipmentDTO> avatars = equipmentService.getUserAvatars(userId, page, size);
        return ResponseEntity.ok(avatars);
    }

    /**
     * POST /api/equipment/{userId}/equip-pet
     * Trang bị pet cho user
     * Body: { "petId": 1 }
     */
    @PostMapping("/{userId}/equip-pet")
    public ResponseEntity<Map<String, Object>> equipPet(
            @PathVariable Long userId,
            @RequestBody Map<String, Long> request) {

        Long petId = request.get("petId");
        Map<String, Object> result = equipmentService.equipPet(userId, petId);
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/equipment/{userId}/equip-avatar
     * Trang bị avatar cho user
     * Body: { "avatarId": 1 }
     */
    @PostMapping("/{userId}/equip-avatar")
    public ResponseEntity<Map<String, Object>> equipAvatar(
            @PathVariable Long userId,
            @RequestBody Map<String, Long> request) {

        Long avatarId = request.get("avatarId");
        Map<String, Object> result = equipmentService.equipAvatar(userId, avatarId);
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/equipment/{userId}/count
     * Đếm số lượng pet và avatar
     */
    @GetMapping("/{userId}/count")
    public ResponseEntity<Map<String, Integer>> getEquipmentCount(@PathVariable Long userId) {
        Map<String, Integer> count = equipmentService.getEquipmentCount(userId);
        return ResponseEntity.ok(count);
    }

    /**
     * GET /api/equipment/{userId}/current
     * Debug: Xem pet và avatar đang trang bị
     */
    @GetMapping("/{userId}/current")
    public ResponseEntity<Map<String, Object>> getCurrentEquipment(@PathVariable Long userId) {
        Map<String, Object> current = equipmentService.getCurrentEquipment(userId);
        return ResponseEntity.ok(current);
    }
}