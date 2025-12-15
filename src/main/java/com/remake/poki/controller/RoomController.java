package com.remake.poki.controller;

import com.remake.poki.dto.OnlineUserDTO;
import com.remake.poki.service.OnlineUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room")
public class RoomController {

    @Autowired
    private OnlineUserService onlineUserService;

    /**
     * GET /api/room/online-users?excludeUserId=1
     */
    @GetMapping("/online-users")
    public ResponseEntity<List<OnlineUserDTO>> getOnlineUsers(
            @RequestParam(required = false) Long excludeUserId
    ) {
        return ResponseEntity.ok(onlineUserService.getAvailableUsers(excludeUserId));
    }
}