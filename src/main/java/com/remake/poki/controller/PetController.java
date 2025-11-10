package com.remake.poki.controller;

import com.remake.poki.model.PetStats;
import com.remake.poki.service.PetService;
import com.remake.poki.service.UserPetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

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

    @PostMapping("/generate-levels")
    public ResponseEntity<List<PetStats>> generatePetLevels(
            @RequestParam Long petId,
            @RequestParam int baseHp,
            @RequestParam int baseAttack,
            @RequestParam int baseMana,
            @RequestParam BigDecimal baseWeaknessValue) {

        return ResponseEntity.ok(petService.generatePetLevels(petId, baseHp, baseAttack, baseMana, baseWeaknessValue));
    }

    @PostMapping("/ups")
    public ResponseEntity<List<PetStats>> ups(
            @RequestParam Long petId,
            @RequestParam BigDecimal baseWeaknessValue) {

        return ResponseEntity.ok(petService.ups(petId, baseWeaknessValue));
    }

    @GetMapping()
    public ResponseEntity<?> getPets() {

        return new ResponseEntity<>(petService.getPets(), HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getEnemyPets(@PathVariable() Long userId) {
        return new ResponseEntity<>(petService.getEnemyPets(userId), HttpStatus.OK);
    }

    @GetMapping("/infoEnemyPet/{petEId}/{petId}")
    public ResponseEntity<?> getInfoEPet(@PathVariable() Long petEId, @PathVariable() Long petId) {
        return new ResponseEntity<>(petService.getInfoEPet(petEId, petId), HttpStatus.OK);
    }
}

