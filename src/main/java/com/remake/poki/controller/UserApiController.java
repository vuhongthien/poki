package com.remake.poki.controller;

import com.remake.poki.model.User;
import com.remake.poki.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApiController {

    private final UserRepository userRepository;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser(@RequestParam Long userId) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }
}