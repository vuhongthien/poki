package com.remake.poki.request;

import lombok.Data;

@Data
public class UseCardRequest {
    private Long userId;
    private Long cardId;
    private int quantity = 1;
}
