package com.remake.poki.service;

import com.remake.poki.dto.StoneDTO;
import com.remake.poki.model.Stone;
import com.remake.poki.repo.StoneRepository;
import com.remake.poki.repo.StoneUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoneService {
    @Autowired
    private StoneRepository stoneRepository;
    @Autowired
    private StoneUserRepository stoneUserRepository;

    public Stone createStone(Stone stone) {
        return stoneRepository.save(stone);
    }

    public List<Stone> saveAll(List<Stone> stone) {
        return stoneRepository.saveAll(stone);
    }

    public List<StoneDTO> getStonesByUser(Long userId) {
        return stoneUserRepository.findAllByUserId(userId);
    }
}
