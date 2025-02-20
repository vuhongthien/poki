package com.remake.poki.controller;

import com.remake.poki.service.PetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/pets")
public class PetController {
    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping("/import")
    public String importPets(@RequestBody String folderPath) {
        petService.createPetsFromImages(folderPath);
        return "Import Pets thành công!";
    }

    @PostMapping("/importName")
    public String importPetsName() throws IOException {
        petService.updatePetNamesFromFile();
        return "Import Pets thành công!";
    }

    @GetMapping()
    public ResponseEntity<?> getPets() {

        return new ResponseEntity<>(petService.getPets(), HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getEnemyPets(@PathVariable() Long userId) {
        return new ResponseEntity<>(petService.getEnemyPets(userId), HttpStatus.OK);
    }
}

