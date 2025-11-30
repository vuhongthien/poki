package com.remake.poki.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
public class UserDTO implements Serializable {

    private Long id;

    private Long petId;

     private Long avtId;

    private int energy;

    private int energyFull;

    private int gold;
    private int ruby;

    private int requestAttack;

    private String name;

    private int lever;

    private int exp;

    private int expCurrent;

    private int wheel;

    private int starWhite ;
    private int starBlue ;
    private int starRed ;
    private long secondsUntilNextRegen;

}
