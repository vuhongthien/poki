package com.remake.poki.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class PetRequest implements Serializable {
    private Long petId;
    private int requestAttack;
    private Integer expGain;
}
