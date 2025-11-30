package com.remake.poki.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String password;

    private String user;
    @Column(name = "last_energy_update")
    private LocalDateTime lastEnergyUpdate;
    private int energy = 80;
    private int energyFull;
    private int lever;
    private int exp = 500;
    private int expCurrent=10;
    private int gold = 10000;
    private int ruby = 1000;
    private int requestAttack = 500;
    private int wheel = 3;
    private int wheelDay = 3;
    private int starWhite = 0;
    private int starBlue = 0;
    private int starRed = 0;
    private Long petId;
    private Long avtId;

}
