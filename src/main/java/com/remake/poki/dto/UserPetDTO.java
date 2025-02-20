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

    private int hp;

    private int attack;

    private int mana;

    private BigDecimal weaknessValue;

    private int manaSkillCard;

    public UserPetDTO(Pet p, UserPet up, PetStats ps, ElementWeakness ew, User u, SkillCard sc) {
        this.id = up.getId();
        this.userId = u.getId();
        this.petId = p.getId();
        this.skillCardId = sc.getId();
        this.name = p.getName();
        this.des = sc.getDescription();
        this.elementType = p.getElementType();
        this.elementOther = ew.getElement();
        this.level = up.getLevel();
        this.hp = ps.getHp();
        this.attack = ps.getAttack();
        this.mana = ps.getMana();
        this.weaknessValue = ps.getWeaknessValue();
        this.manaSkillCard = sc.getMana();
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
}
