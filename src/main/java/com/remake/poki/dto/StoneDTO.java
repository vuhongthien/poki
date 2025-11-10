package com.remake.poki.dto;

import com.remake.poki.enums.ElementType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoneDTO {
    private Long idUser;
    private Long idStone;
    private int count;
    private String name;
    private int lever;
    private ElementType elementType;
}
