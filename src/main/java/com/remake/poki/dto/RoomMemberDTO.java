package com.remake.poki.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class RoomMemberDTO {
    // ✅ MEMBER INFO (cho multiplayer)
    private Long userId;
    private String username;
    private String avatarId;
    private int level;
    private boolean ready;
    private boolean host;

    // ✅ USER GAME INFO (từ UserDTO)
    private int energy;
    private int energyFull;
    private int count;
    private int requestPass;
    private int requestAttack;
    private int petId;
    private int enemyPetId;
    private String nameEnemyPetId;
    private String elementType;
    private List<CardDTO> cards;
    private List<CardDTO> cardsSelected;
    private List<UserPetDTO> userPets;

    // ✅ Constructor mặc định
    public RoomMemberDTO() {
        this.cardsSelected = new ArrayList<>();
    }

    // ✅ Constructor đơn giản (cho quick setup)
    public RoomMemberDTO(Long userId, String username, String avatarId, int level, boolean isHost) {
        this.userId = userId;
        this.username = username;
        this.avatarId = avatarId;
        this.level = level;
        this.host = isHost;
        this.ready = isHost; // Host luôn ready
        this.cardsSelected = new ArrayList<>();
    }

    // ✅ Constructor đầy đủ (từ UserDTO + RoomDTO)
    public RoomMemberDTO(Long userId, String username, String avatarId, int level, boolean host,
                         int energy, int energyFull, int count, int requestPass, int requestAttack,
                         int petId, int enemyPetId, String nameEnemyPetId, String elementType,
                         List<CardDTO> cards, List<UserPetDTO> userPets) {
        this.userId = userId;
        this.username = username;
        this.avatarId = avatarId;
        this.level = level;
        this.host = host;
        this.ready = false;

        // User game info
        this.energy = energy;
        this.energyFull = energyFull;
        this.count = count;
        this.requestPass = requestPass;
        this.requestAttack = requestAttack;
        this.petId = petId;
        this.enemyPetId = enemyPetId;
        this.nameEnemyPetId = nameEnemyPetId;
        this.elementType = elementType;
        this.cards = cards != null ? cards : new ArrayList<>();
        this.userPets = userPets != null ? userPets : new ArrayList<>();
        this.cardsSelected = new ArrayList<>();
    }
}