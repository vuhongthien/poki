package com.remake.poki.dto;

import java.util.List;

public class GroupDTO {
    private Long id;

    private String name;

    private List<PetEnemyDTO> listPetEnemy;

    public GroupDTO(Long id, String name, List<PetEnemyDTO> listPetEnemy) {
        this.id = id;
        this.name = name;
        this.listPetEnemy = listPetEnemy;
    }

    public GroupDTO(Long id, String name) {
        this.id = id;
        this.name = name;
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

    public List<PetEnemyDTO> getListPetEnemy() {
        return listPetEnemy;
    }

    public void setListPetEnemy(List<PetEnemyDTO> listPetEnemy) {
        this.listPetEnemy = listPetEnemy;
    }
}
