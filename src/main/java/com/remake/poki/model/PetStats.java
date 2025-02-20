package com.remake.poki.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "pet_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long petId;

    private int level;

    private int hp;

    private int attack;

    private int mana;

    @Column(precision = 5, scale = 2)
    private BigDecimal weaknessValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public BigDecimal getWeaknessValue() {
        return weaknessValue;
    }

    public void setWeaknessValue(BigDecimal weaknessValue) {
        this.weaknessValue = weaknessValue;
    }
}

