package com.remake.poki.dto;

import lombok.NoArgsConstructor;

import java.io.Serializable;

public class UserDTO implements Serializable {

    private Long id;

    private int energy;

    private int energyFull;

    private int gold;

    private int requestAttack;

    public UserDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getEnergyFull() {
        return energyFull;
    }

    public void setEnergyFull(int energyFull) {
        this.energyFull = energyFull;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getRequestAttack() {
        return requestAttack;
    }

    public void setRequestAttack(int requestAttack) {
        this.requestAttack = requestAttack;
    }
}
