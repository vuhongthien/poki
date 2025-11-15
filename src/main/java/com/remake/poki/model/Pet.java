package com.remake.poki.model;
import com.remake.poki.enums.ElementType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pets")
@NoArgsConstructor
@AllArgsConstructor
public class Pet {

    @Id
    private Long id;

    private Long skillCardId;

    private String name;

    @Setter
    private String des;

    @Enumerated(EnumType.STRING)
    private ElementType elementType;

    private int maxLevel;

    private int parentId;
    private int no;

    // Flag đánh dấu pet huyền thoại
    private boolean flagLegend = false;

    public boolean isFlagLegend() {
        return flagLegend;
    }

    public void setFlagLegend(boolean flagLegend) {
        this.flagLegend = flagLegend;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
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

    public String getDes() {
        return des;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }
}