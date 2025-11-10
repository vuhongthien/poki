package com.remake.poki.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStarResponse {
    private boolean success;
    private String message;
    private int starWhite;
    private int starBlue;
    private int starRed;
}