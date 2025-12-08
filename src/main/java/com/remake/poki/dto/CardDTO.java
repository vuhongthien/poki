package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {

    private Long id;
    private Long cardId;
    private String name;
    private String description;
    private String elementTypeCard;
    private int value;
    private int maxLever;
    private int count;
    private int level;
    private int power;
    private int green;
    private int blue;
    private int red ;
    private int yellow ;
    private int white ;
    private Long conditionUse;
}
