package com.remake.poki.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String password;

    private String user;

    private int energy = 80;

    private int energyFull;

    private int lever;

    private int exp = 500;

    private int expCurrent=10;


    private int gold = 10000;

    private int requestAttack = 500;

    private int wheel = 3;
    private int starWhite = 0;
    private int starBlue = 0;
    private int starRed = 0;

    private Long petId;

    private Long avtId;

    public Long getAvtId() {
        return avtId;
    }

    public void setAvtId(Long avtId) {
        this.avtId = avtId;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

    public int getLever() {
        return lever;
    }

    public void setLever(int lever) {
        this.lever = lever;
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
