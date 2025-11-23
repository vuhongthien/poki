package com.remake.poki.dto;

import lombok.NoArgsConstructor;

import java.io.Serializable;

public class UserDTO implements Serializable {

    private Long id;

    private Long petId;

     private Long avtId;

    private int energy;

    private int energyFull;

    private int gold;

    private int requestAttack;

    private String name;

    private int lever;

    private int exp;

    private int expCurrent;

    private int wheel;

    private int starWhite ;
    private int starBlue ;
    private int starRed ;
    private long secondsUntilNextRegen;

    public long getSecondsUntilNextRegen() {
        return secondsUntilNextRegen;
    }

    public void setSecondsUntilNextRegen(long secondsUntilNextRegen) {
        this.secondsUntilNextRegen = secondsUntilNextRegen;
    }

    public UserDTO() {
    }

    public Long getAvtId() {
        return avtId;
    }

    public void setAvtId(Long avtId) {
        this.avtId = avtId;
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

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public int getWheel() {
        return wheel;
    }

    public void setWheel(int wheel) {
        this.wheel = wheel;
    }

    public int getStarWhite() {
        return starWhite;
    }

    public void setStarWhite(int starWhite) {
        this.starWhite = starWhite;
    }

    public int getStarBlue() {
        return starBlue;
    }

    public void setStarBlue(int starBlue) {
        this.starBlue = starBlue;
    }

    public int getStarRed() {
        return starRed;
    }

    public void setStarRed(int starRed) {
        this.starRed = starRed;
    }
}
