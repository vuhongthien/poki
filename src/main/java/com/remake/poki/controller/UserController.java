package com.remake.poki.controller;

import com.remake.poki.ApiResponse;
import com.remake.poki.dto.LoginDTO;
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

}
