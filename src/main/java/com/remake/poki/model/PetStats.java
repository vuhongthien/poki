package com.remake.poki.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "pet_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long petId;

    private int level;

    private int hp;

    private int attack;

    private int mana;

    @Column(precision = 5, scale = 2)
    private BigDecimal weaknessValue;
}

