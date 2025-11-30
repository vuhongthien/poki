package com.remake.poki.model;
import com.remake.poki.enums.ElementType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pets")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Pet {

    @Id
    private Long id;

    private Long skillCardId;

    private String name;

    @Setter
    private String des;

    @Enumerated(EnumType.STRING)
    private ElementType elementType;

    private int maxLevel;

    private int parentId;
    private Long childId;
    private int no;

    // Flag đánh dấu pet huyền thoại
    private boolean flagLegend = false;

}