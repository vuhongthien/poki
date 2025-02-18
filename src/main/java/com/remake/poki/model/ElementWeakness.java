package com.remake.poki.model;
import com.remake.poki.enums.ElementType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "element_weakness")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ElementWeakness {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ElementType element;

    @Enumerated(EnumType.STRING)
    private ElementType weakAgainst;
}