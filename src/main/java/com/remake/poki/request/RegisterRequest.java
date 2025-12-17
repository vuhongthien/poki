package com.remake.poki.request;

import lombok.*;

@Data
public class RegisterRequest {
    private String user;
    private String name;
    private String password;
    private String deviceId;
}
