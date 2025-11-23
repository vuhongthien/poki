package com.remake.poki.dto;

import com.remake.poki.enums.ElementType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDTO {
    private Long userId;
    private String userName;
    private int level;
    private Long currentPetId;
    private Long avtId;
    
    // Thông tin pet đang dùng
    private PetDetailInfo currentPet;
    
    // Danh sách tất cả pet
    private List<UserPetInfo> allPets;
    
    // Danh sách đá nâng cấp
    private List<StoneInfo> stones;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PetDetailInfo {
        private Long petId;
        private String petName;
        private int level;
        private int attack;
        private int hp;
        private int mana;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPetInfo {
        private Long petId;
        private int level;
        private ElementType elementType;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoneInfo {
        private Long stoneId;
        private String stoneName;
        private int count;
        private int level;
        private ElementType elementType;
    }
}
