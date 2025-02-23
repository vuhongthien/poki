package com.remake.poki.controller;

import com.remake.poki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
