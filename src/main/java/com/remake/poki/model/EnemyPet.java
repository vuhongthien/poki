package com.remake.poki.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "enemy_pet")
public class EnemyPet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idPet;

    private Long idGroupPet;

    private int lever;

    private int leverDisplay;

    private int requestPass;

    private int requestAttack;

    private Long parentId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdPet() {
        return idPet;
    }

    public void setIdPet(Long idPet) {
        this.idPet = idPet;
    }

    public Long getIdGroupPet() {
        return idGroupPet;
    }

    public void setIdGroupPet(Long idGroupPet) {
        this.idGroupPet = idGroupPet;
    }

    public int getLever() {
        return lever;
    }

    public void setLever(int lever) {
        this.lever = lever;
    }

    public int getLeverDisplay() {
        return leverDisplay;
    }

    public void setLeverDisplay(int leverDisplay) {
        this.leverDisplay = leverDisplay;
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
