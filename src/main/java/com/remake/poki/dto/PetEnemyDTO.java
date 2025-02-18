package com.remake.poki.dto;

public class PetEnemyDTO {

    private Long id;

    private String name;

    private int lever;

    private int leverDisplay;

    private int count;

    private int requestPass;

    private int requestAttack;

    public PetEnemyDTO(Long id, String name, int lever, int leverDisplay, int count, int requestPass, int requestAttack) {
        this.id = id;
        this.name = name;
        this.lever = lever;
        this.leverDisplay = leverDisplay;
        this.count = count;
        this.requestPass = requestPass;
        this.requestAttack = requestAttack;
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
}
