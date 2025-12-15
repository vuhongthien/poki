package com.remake.poki.dto;

import lombok.Data;

@Data
public class RoomInviteDTO {
    private Long inviteId;
    private Long roomId;
    private Long fromUserId;
    private String fromUsername;
    private Long toUserId;
    private String message;
    private long timestamp;
    private String status; // PENDING, ACCEPTED, DECLINED
}