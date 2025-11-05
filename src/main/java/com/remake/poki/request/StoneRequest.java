package com.remake.poki.request;

import lombok.Data;

@Data
public class StoneRequest {
    private String element;
    private Integer level;
    private Integer quantity;
}
