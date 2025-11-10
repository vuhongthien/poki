package com.remake.poki.dto;

import com.remake.poki.enums.ElementType;
import com.remake.poki.model.*;
import jakarta.persistence.Column;

import java.math.BigDecimal;

public class UserPetDTO {
    private Long id;

    private Long userId;
    //pet

    private Long petId;

    private Long skillCardId;

    private String name;

    private String des;

    private ElementType elementType;

    private ElementType elementOther;

    private int level;
    private int maxLevel;

    private int hp;

    private int attack;

    private int mana;

    private BigDecimal weaknessValue;

    private int manaSkillCard;

    public UserPetDTO(Pet p, UserPet up, PetStats ps, ElementWeakness ew, User u, SkillCard sc) {
        this.id = (up != null) ? up.getId() : null;
        this.userId = (u != null) ? u.getId() : null;
        this.petId = (p != null) ? p.getId() : null;
        this.skillCardId = (sc != null) ? sc.getId() : null;
        this.name = (p != null) ? p.getName() : null;
        this.des = (sc != null) ? sc.getDescription() : null;
        this.elementType = (p != null) ? p.getElementType() : null;
        this.elementOther = (ew != null) ? ew.getElement() : null;
        this.level = (up != null) ? up.getLevel() : 0;
        this.maxLevel = (p != null) ? p.getMaxLevel() : 0;
        this.hp = (ps != null) ? ps.getHp() : 0;
        this.attack = (ps != null) ? ps.getAttack() : 0;
        this.mana = (ps != null) ? ps.getMana() : 0;
        this.weaknessValue = (ps != null) ? ps.getWeaknessValue() : BigDecimal.ZERO;
        this.manaSkillCard = (sc != null) ? sc.getMana() : 0;
    }

    public UserPetDTO(Pet p, EnemyPet up, PetStats ps, ElementWeakness ew) {
        this.id = (up != null) ? up.getId() : null;
        this.petId = (p != null) ? p.getId() : null;
        this.name = (p != null) ? p.getName() : null;
        this.elementType = (p != null) ? p.getElementType() : null;
        this.elementOther = (ew != null) ? ew.getElement() : null;
        this.level = (up != null) ? up.getLever() : 0;
        this.hp = (ps != null) ? ps.getHp() : 0;
        this.attack = (ps != null) ? ps.getAttack() : 0;
        this.mana = (ps != null) ? ps.getMana() : 0;
        this.weaknessValue = (ps != null) ? ps.getWeaknessValue() : BigDecimal.ZERO;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
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

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public void setElementType(ElementType elementType) {
        this.elementType = elementType;
    }

    public ElementType getElementOther() {
        return elementOther;
    }

    public void setElementOther(ElementType elementOther) {
        this.elementOther = elementOther;
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

    public int getManaSkillCard() {
        return manaSkillCard;
    }

    public void setManaSkillCard(int manaSkillCard) {
        this.manaSkillCard = manaSkillCard;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }
}
