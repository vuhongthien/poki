package com.remake.poki.dto;

import com.remake.poki.model.CountPass;
import com.remake.poki.model.EnemyPet;
import com.remake.poki.model.Pet;
import com.remake.poki.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
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
    private Long avtId;

    private String nameEnemyPetId;

    private String elementType;

    private List<CardDTO> cards;

    public UserRoomDTO(User u, CountPass cp, Pet p, EnemyPet ep) {
        this.id = u.getId();
        this.avtId = u.getAvtId();
        this.energy = u.getEnergy();
        this.energyFull = u.getEnergyFull();
        this.count = (cp != null) ? cp.getCount() : 0;
        this.requestPass = ep.getRequestPass();
        this.requestAttack = ep.getRequestAttack();
        this.name = u.getName();
        this.lever = u.getLever();
        this.petId = p.getId();
        this.enemyPetId = ep.getIdPet();
        this.nameEnemyPetId = p.getName();
        this.elementType = p.getElementType().name();
    }

}
