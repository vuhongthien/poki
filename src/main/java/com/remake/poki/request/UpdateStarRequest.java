package com.remake.poki.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStarRequest {
    private Long userId;
    private String starType; // "white", "blue", "red"
    private int amount;
}
