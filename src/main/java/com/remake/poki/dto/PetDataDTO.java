package com.remake.poki.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetDataDTO {
    private Long petId;
    private String name;
    private boolean unlocked;
    private int level;
    private String imageUrl;
}
