package com.remake.poki.request;

import lombok.Data;

import java.util.List;

@Data
public class GameReward {
    private List<RewardStone> stones;
    private Boolean receivedPet;
    private String petElement;
    private Integer petId;
}