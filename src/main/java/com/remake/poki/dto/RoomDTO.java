package com.remake.poki.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RoomDTO {
    // ✅ EXISTING FIELDS - TỪ UNITY
    private int id;
    private int energy;
    private int energyFull;
    private int count;
    private int requestPass;
    private int requestAttack;
    private String name;
    private int lever;
    private int petId;
    private int enemyPetId;
    private String nameEnemyPetId;
    private String elementType;


    // ✅ NEW FIELDS - CHO MULTIPLAYER
    private Long roomId;           // ID của room (để join)
    private Long hostUserId;       // ID của người tạo room
    private String hostUsername;   // Tên của người tạo room
    private List<RoomMemberDTO> members;
    private String status;         // WAITING, READY, IN_MATCH
    private int maxPlayers;

    public RoomDTO() {
        this.members = new ArrayList<>();
        this.status = "WAITING";
        this.maxPlayers = 3;
    }

    // ✅ Helper method
    public boolean isFull() {
        return members != null && members.size() >= maxPlayers;
    }
}