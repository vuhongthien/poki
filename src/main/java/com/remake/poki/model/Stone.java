package com.remake.poki.model;

import com.remake.poki.enums.ElementType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stones")
public class Stone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private int lever;

    @Enumerated(EnumType.STRING)
    private ElementType elementType;
}
