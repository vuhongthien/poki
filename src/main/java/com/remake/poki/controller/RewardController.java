package com.remake.poki.controller;

import com.remake.poki.ApiResponse;
import com.remake.poki.request.PetRequest;
import com.remake.poki.request.StoneRequest;
import com.remake.poki.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reward")
public class RewardController {
    @Autowired
    private RewardService rewardService;

    @PostMapping("/{userId}/countPass")
    public ResponseEntity<?> countPass(@PathVariable Long userId, @RequestBody PetRequest request) {

        try {
            rewardService.upCountPass(userId, request);

            return ResponseEntity.ok().body(new ApiResponse(true, "count successfully", null));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PostMapping("/{userId}/pets")
    public ResponseEntity<?> addPetToUser(@PathVariable Long userId, @RequestBody PetRequest request) {

        try {
            rewardService.addPetToUser(userId, request.getPetId());

            return ResponseEntity.ok().body(new ApiResponse(true, "Pet added successfully", null));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PostMapping("/{userId}/{petId}/stones")
    public ResponseEntity<?> addStoneToUser(@PathVariable Long userId,@PathVariable Long petId, @RequestBody StoneRequest request) {

        try {
            if (!isValidElement(request.getElement())) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Invalid element type", null));
            }

            if (request.getLevel() < 1 || request.getLevel() > 7) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Level must be between 1-7", null));
            }

            if (request.getQuantity() < 1) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Quantity must be positive", null));
            }

            rewardService.addStoneToUser(userId, request.getElement(), request.getLevel(), request.getQuantity(), petId);

            return ResponseEntity.ok().body(new ApiResponse(true, "Stone added successfully", null));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Validate element type
     */
    private boolean isValidElement(String element) {
        return element != null && (element.equalsIgnoreCase("Fire") || element.equalsIgnoreCase("Water") || element.equalsIgnoreCase("Earth") || element.equalsIgnoreCase("Wood") || element.equalsIgnoreCase("Metal"));
    }
}
