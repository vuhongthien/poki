package com.remake.poki.service;

import com.remake.poki.model.Stone;
import com.remake.poki.repo.StoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoneService {
    @Autowired
    private StoneRepository stoneRepository;

    public Stone createStone(Stone stone) {
        return stoneRepository.save(stone);
    }

    public List<Stone> saveAll(List<Stone> stone) {
        return stoneRepository.saveAll(stone);
    }
}
