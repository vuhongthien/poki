package com.remake.poki.controller;

import com.remake.poki.model.PetStats;
import com.remake.poki.model.Stone;
import com.remake.poki.service.RewardService;
import com.remake.poki.service.StoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

}
