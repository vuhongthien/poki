package com.remake.poki.response;

import lombok.Data;

@Data
public class UseCardResponse {
    private boolean success;
    private String message;
    private int remainingCount;
}
