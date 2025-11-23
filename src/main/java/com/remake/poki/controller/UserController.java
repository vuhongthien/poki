package com.remake.poki.controller;

import com.remake.poki.ApiResponse;
import com.remake.poki.dto.DeductGoldRequestDTO;
import com.remake.poki.dto.DeductGoldResponseDTO;
import com.remake.poki.dto.LoginDTO;
import com.remake.poki.dto.UserDTO;
import com.remake.poki.request.UpdateStarRequest;
import com.remake.poki.response.UpdateStarResponse;
import com.remake.poki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getMoney(@PathVariable() Long userId) {
        UserDTO userDTO = userService.getMoney(userId);
        long secondsUntilNext = userService.getSecondsUntilNextRegen( userId);
        userDTO.setSecondsUntilNextRegen(secondsUntilNext);
        return new ResponseEntity<>(userService.getMoney(userId), HttpStatus.OK);
    }

    @GetMapping("/room/{userId}/{enemyPetId}")
    public ResponseEntity<?> getInfoRoom(@PathVariable() Long userId, @PathVariable() Long enemyPetId) {
        return new ResponseEntity<>(userService.getInfoRoom(userId, enemyPetId), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody() LoginDTO request) {
        return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
    }

    @GetMapping("/energy/{userId}")
    public ResponseEntity<?> downEnergy(@PathVariable() Long userId) {

        return ResponseEntity.ok().body(new ApiResponse(true, userService.downEnergy(userId), null));
    }

    @GetMapping("/ct/{userId}/{requestAttack}")
    public ResponseEntity<?> upCT(@PathVariable() Long userId, @PathVariable() int requestAttack) {

        return ResponseEntity.ok().body(new ApiResponse(true, userService.upCT(userId, requestAttack), null));
    }

    @PostMapping("/deduct-gold")
    public ResponseEntity<DeductGoldResponseDTO> deductGold(@RequestBody DeductGoldRequestDTO request) {
        DeductGoldResponseDTO response = userService.deductGold(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-star")
    public ResponseEntity<UpdateStarResponse> updateStar(@RequestBody UpdateStarRequest request) {
        try {
            // Validate request
            if (request.getUserId() == null) {
                UpdateStarResponse errorResponse = new UpdateStarResponse();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("User ID is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (request.getStarType() == null || request.getStarType().trim().isEmpty()) {
                UpdateStarResponse errorResponse = new UpdateStarResponse();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Star type is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (request.getAmount() <= 0) {
                UpdateStarResponse errorResponse = new UpdateStarResponse();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Amount must be greater than 0");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Gọi service để cập nhật
            UpdateStarResponse response = userService.updateStar(request);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        } catch (Exception e) {
            UpdateStarResponse errorResponse = new UpdateStarResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
