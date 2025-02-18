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

    private boolean checkNew;

    private int requestPass;

    private int requestAttack;

    private Long parentId;
}
