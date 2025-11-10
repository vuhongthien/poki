package com.remake.poki.controller;

import com.remake.poki.service.UserPetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userPets")
public class UserPetController {
    final
    UserPetService userPetService;

    public UserPetController(UserPetService userPetService) {
        this.userPetService = userPetService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserPets(@PathVariable() Long userId) {
        return new ResponseEntity<>(userPetService.getUserPets(userId), HttpStatus.OK);
    }

    @GetMapping("/match/{userId}/{petId}/{ePetId}")
    public ResponseEntity<?> getInfoMatch(@PathVariable() Long userId, @PathVariable() Long petId, @PathVariable() Long ePetId) {
        return new ResponseEntity<>(userPetService.getInfoMatch(userId, petId, ePetId), HttpStatus.OK);
    }

    @GetMapping("/update/{userId}/{petId}")
    public ResponseEntity<?> updatePets(@PathVariable() Long userId, @PathVariable() Long petId) {

        return new ResponseEntity<>(userPetService.updatePets(userId, petId), HttpStatus.OK);
    }

}
