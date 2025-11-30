package com.remake.poki.controller;


import com.remake.poki.model.User;
import com.remake.poki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wheelLegend")
public class WheelLegendController {

    @Autowired
    private UserService userService;

    // API kiểm tra số lượt quay còn lại
    @GetMapping("/check/{userId}")
    public ResponseEntity<?> checkWheel(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("User not found"));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("wheel", user.getWheel());
            response.put("hasWheel", user.getWheel() > 0);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error checking wheel: " + e.getMessage()));
        }
    }

    // API trừ 1 lượt quay
    @PostMapping("/spin/{userId}")
    public ResponseEntity<?> spinWheel(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("User not found"));
            }

            if (user.getWheel() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("No wheel turns remaining"));
            }

            // Trừ 1 lượt quay
            user.setWheel(user.getWheel() - 1);
            userService.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Wheel spin successful");
            response.put("remainingWheel", user.getWheel());
            response.put("userId", userId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error spinning wheel: " + e.getMessage()));
        }
    }

    // API cộng thêm lượt quay (optional - dùng cho admin hoặc reward)
    @PostMapping("/add/{userId}")
    public ResponseEntity<?> addWheel(@PathVariable Long userId, @RequestParam int amount) {
        try {
            User user = userService.findById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("User not found"));
            }

            user.setWheel(user.getWheel() + amount);
            userService.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Wheel added successfully");
            response.put("totalWheel", user.getWheel());
            response.put("addedAmount", amount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error adding wheel: " + e.getMessage()));
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        return error;
    }
}