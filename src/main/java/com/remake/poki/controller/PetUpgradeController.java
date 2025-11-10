package com.remake.poki.controller;

import com.remake.poki.request.PetUpgradeRequest;
import com.remake.poki.response.PetUpgradeResponse;
import com.remake.poki.service.PetUpgradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/upgrade")
public class PetUpgradeController {

    private final PetUpgradeService petUpgradeService;

    public PetUpgradeController(PetUpgradeService petUpgradeService) {
        this.petUpgradeService = petUpgradeService;
    }

    @PostMapping("")
    public ResponseEntity<PetUpgradeResponse> upgradePet(@RequestBody PetUpgradeRequest request) {
        try {
            PetUpgradeResponse response = petUpgradeService.upgradePet(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new PetUpgradeResponse(false, e.getMessage(), null)
            );
        }
    }
}
