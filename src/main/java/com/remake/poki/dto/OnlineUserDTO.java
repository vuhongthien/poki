package com.remake.poki.dto;

import lombok.Data;

@Data
public class OnlineUserDTO {
    private Long userId;
    private String username;
    private String avatarId;
    private int level;
    private boolean inMatch;
    private Long roomId; // null nếu chưa vào phòng
    private Integer petId;           // Pet hiện tại
    private Integer energy;          // Năng lượng hiện tại
    private Integer energyFull;
    private Integer count;
    private Integer requestPass;
}