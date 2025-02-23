package com.remake.poki.dto;

import com.remake.poki.model.CountPass;
import com.remake.poki.model.EnemyPet;
import com.remake.poki.model.Pet;
import com.remake.poki.model.User;

public class UserRoomDTO {
    private Long id;

    private int energy;

    private int energyFull;

    private int count;

    private int requestPass;

    private int requestAttack;

    private String name;

    private int lever;

    private Long petId;

    private Long enemyPetId;

    private String nameEnemyPetId;

    public UserRoomDTO(User u, CountPass cp, Pet p, EnemyPet ep) {
        this.id = u.getId();
        this.energy = u.getEnergy();
        this.energyFull = u.getEnergyFull();
        this.count = (cp != null) ? cp.getCount() : 0;
        this.requestPass = ep.getRequestPass();
        this.requestAttack = ep.getRequestAttack();
        this.name = u.getName();
        this.lever = u.getLever();
        this.petId = p.getId();
        this.enemyPetId = ep.getIdPet();
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getRequestPass() {
        return requestPass;
    }

    public void setRequestPass(int requestPass) {
        this.requestPass = requestPass;
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

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getEnemyPetId() {
        return enemyPetId;
    }

    public void setEnemyPetId(Long enemyPetId) {
        this.enemyPetId = enemyPetId;
    }

    public String getNameEnemyPetId() {
        return nameEnemyPetId;
    }

    public void setNameEnemyPetId(String nameEnemyPetId) {
        this.nameEnemyPetId = nameEnemyPetId;
    }
}
