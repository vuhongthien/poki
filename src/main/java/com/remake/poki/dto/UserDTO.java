package com.remake.poki.dto;

import lombok.NoArgsConstructor;

import java.io.Serializable;

public class UserDTO implements Serializable {

    private Long id;

    private int energy;

    private int energyFull;

    private int gold;

    private int requestAttack;

    private String name;

    private int lever;

    private int exp;

    private int expCurrent;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLever() {
        return lever;
    }

    public void setLever(int lever) {
        this.lever = lever;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getExpCurrent() {
        return expCurrent;
    }

    public void setExpCurrent(int expCurrent) {
        this.expCurrent = expCurrent;
    }
}
