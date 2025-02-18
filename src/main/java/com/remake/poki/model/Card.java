package com.remake.poki.model;

import com.remake.poki.enums.ElementType;
import com.remake.poki.enums.ElementTypeCard;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private ElementTypeCard elementTypeCard;

    private int value;

    private int maxLever;

    private Long conditionUse;
}
