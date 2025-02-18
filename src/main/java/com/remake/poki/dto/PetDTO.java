package com.remake.poki.dto;

import com.remake.poki.enums.ElementType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

public class PetDTO implements Serializable {
    private Long id;

    private Long skillCardId;

    private String name;

    private ElementType elementType;

    private int maxLevel;

    private ElementType elementOther;

    private int hp;

    private int attack;

    private int mana;

    private BigDecimal weaknessValue;

    private String des;

    public PetDTO(Long id, Long skillCardId, String name, ElementType elementType, int maxLevel, ElementType elementOther, int hp, int attack, int mana, BigDecimal weaknessValue, String des) {
        this.id = id;
        this.skillCardId = skillCardId;
        this.name = name;
        this.elementType = elementType;
        this.maxLevel = maxLevel;
        this.elementOther = elementOther;
        this.hp = hp;
        this.attack = attack;
        this.mana = mana;
        this.weaknessValue = weaknessValue;
        this.des = des;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSkillCardId() {
        return skillCardId;
    }

    public void setSkillCardId(Long skillCardId) {
        this.skillCardId = skillCardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public void setElementType(ElementType elementType) {
        this.elementType = elementType;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public ElementType getElementOther() {
        return elementOther;
    }

    public void setElementOther(ElementType elementOther) {
        this.elementOther = elementOther;
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

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
