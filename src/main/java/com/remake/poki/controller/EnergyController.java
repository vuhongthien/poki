package com.remake.poki.controller;

import com.remake.poki.dto.EnergyInfoDTO;
import com.remake.poki.model.User;
import com.remake.poki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/energy")
public class EnergyController {

    @Autowired
    private UserService userService;

    /**
     * Lấy thông tin năng lượng hiện tại (tự động update)
     */
    @GetMapping("/{userId}")
    public ResponseEntity<EnergyInfoDTO> getEnergyInfo(@PathVariable Long userId) {
        // Tự động update năng lượng trước khi trả về
        User user = userService.updateEnergy(userId);

        long secondsUntilNext = userService.getSecondsUntilNextRegen(userId);

        EnergyInfoDTO response = new EnergyInfoDTO(
                user.getEnergy(),
                user.getEnergyFull(),
                secondsUntilNext,
                user.getLastEnergyUpdate() != null
                        ? user.getLastEnergyUpdate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        : null
        );

        return ResponseEntity.ok(response);
    }


}