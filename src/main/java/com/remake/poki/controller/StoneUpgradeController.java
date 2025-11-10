package com.remake.poki.controller;

import com.remake.poki.dto.StoneBatchUpgradeRequestDTO;
import com.remake.poki.dto.StoneBatchUpgradeResponseDTO;
import com.remake.poki.dto.StoneUpgradeRequestDTO;
import com.remake.poki.dto.StoneUpgradeResponseDTO;
import com.remake.poki.service.StoneUpgradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/upgrade")
@RequiredArgsConstructor
public class StoneUpgradeController {

    private final StoneUpgradeService stoneUpgradeService;

    @PostMapping("/stones")
    public ResponseEntity<StoneUpgradeResponseDTO> upgradeStone(@RequestBody StoneUpgradeRequestDTO request) {
        StoneUpgradeResponseDTO response = stoneUpgradeService.upgradeStone(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/batch-upgrade-stones")
    public ResponseEntity<StoneBatchUpgradeResponseDTO> batchUpgradeStones(
            @RequestBody StoneBatchUpgradeRequestDTO request) {

        StoneBatchUpgradeResponseDTO response = stoneUpgradeService.batchUpgradeStones(request);
        return ResponseEntity.ok(response);
    }
}
