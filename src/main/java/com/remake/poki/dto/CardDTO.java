package com.remake.poki.dto;

import com.remake.poki.enums.ElementTypeCard;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class CardDTO {

    private Long id;

    private String name;

    private String description;

    private ElementTypeCard elementTypeCard;

    private int value;

    private int maxLever;

    private Long conditionUse;

    public CardDTO(Long id, String name, String description, ElementTypeCard elementTypeCard, int value, int maxLever, Long conditionUse) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.elementTypeCard = elementTypeCard;
        this.value = value;
        this.maxLever = maxLever;
        this.conditionUse = conditionUse;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ElementTypeCard getElementTypeCard() {
        return elementTypeCard;
    }

    public void setElementTypeCard(ElementTypeCard elementTypeCard) {
        this.elementTypeCard = elementTypeCard;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getMaxLever() {
        return maxLever;
    }

    public void setMaxLever(int maxLever) {
        this.maxLever = maxLever;
    }

    public Long getConditionUse() {
        return conditionUse;
    }

    public void setConditionUse(Long conditionUse) {
        this.conditionUse = conditionUse;
    }
}
