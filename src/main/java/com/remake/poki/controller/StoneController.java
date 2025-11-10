package com.remake.poki.controller;

import com.remake.poki.dto.StoneDTO;
import com.remake.poki.enums.ElementType;
import com.remake.poki.model.PetStats;
import com.remake.poki.model.Stone;
import com.remake.poki.service.RewardService;
import com.remake.poki.service.StoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stone")
public class StoneController {
    @Autowired
    private StoneService stoneService;

    //create
    @PostMapping()
    public ResponseEntity<?> createAll(@RequestBody List<Stone> stones) {
        stoneService.saveAll(stones);
        return ResponseEntity.ok("Saved " + stones.size() + " stones");
    }

    @GetMapping("/{userId}")
    public Map<ElementType, List<StoneDTO>> getUserStonesGrouped(@PathVariable Long userId) {
        return stoneService.getStonesByUser(userId).stream()
                .collect(Collectors.groupingBy(StoneDTO::getElementType,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparingInt(StoneDTO::getLever))
                                        .toList()
                        )));
    }

}
