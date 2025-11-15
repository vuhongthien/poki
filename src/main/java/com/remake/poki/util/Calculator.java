package com.remake.poki.util;

import com.remake.poki.dto.UserPetDTO;
import com.remake.poki.dto.PetDTO;

import java.math.BigDecimal;

public class Calculator {

    // Cấu hình chung
    private static final int DEFAULT_MAX_LEVEL = 12;

    // Tăng cơ bản mỗi level (áp dụng cho mọi pet)
    private static final double ATK_GROWTH_PER_LEVEL  = 1.08; // +8%
    private static final double HP_GROWTH_PER_LEVEL   = 1.09; // +9%
    private static final double MANA_GROWTH_PER_LEVEL = 1.07; // +7%

    // Mốc buff
    private static final int MILESTONE_LV1 = 7;
    private static final double MILESTONE_LV1_BONUS = 1.05; // +5% khi >= 7
    private static final int MILESTONE_LV2 = 10;
    private static final double MILESTONE_LV2_BONUS = 1.07; // +7% khi >= 10

    /**
     * Case 1:
     *  - Input: UserPetDTO (đã có level hiện tại)
     *  - Base stat lấy ngay từ chính UserPetDTO (thường dùng nếu đang lưu base ở lv1)
     *  - Output: UserPetDTO sau khi scale stat theo level + buff lv7/lv10
     */
    public static UserPetDTO calculateStats(UserPetDTO userPet) {
        return calculateStats(userPet, null, 0);
    }
    public static UserPetDTO calculateStats(UserPetDTO userPet, int level) {
        return calculateStats(userPet, null, level);
    }

    /**
     * Case 2 (khuyên dùng):
     *  - Input:
     *      + userPet: chứa thông tin user, level hiện tại,...
     *      + petBase: PetDTO (base config: hp/atk/mana/maxLevel/element/weakness)
     *  - Base stat lấy từ PetDTO (ổn định, không sợ bị nhân chồng)
     *  - Output: cập nhật lại chỉ số vào UserPetDTO và trả về
     */
    public static UserPetDTO calculateStats(UserPetDTO userPet, PetDTO petBase, int level) {
        if (userPet == null) return null;

        // Level & MaxLevel
        level = userPet.getLevel();


        int maxLevel =
                (userPet.getMaxLevel() > 0) ? userPet.getMaxLevel()
                        : (petBase != null && petBase.getMaxLevel() > 0) ? petBase.getMaxLevel()
                        : DEFAULT_MAX_LEVEL;
        if(level == 14){
            maxLevel = 14;
        }

        if (level < 1) level = 1;
        if (level > maxLevel) level = maxLevel;

        // Base stats:
        // Ưu tiên lấy từ PetDTO (pet gốc), nếu không có thì fallback sang UserPetDTO
        int baseAtk  = (petBase != null) ? petBase.getAttack() : userPet.getAttack();
        int baseHp   = (petBase != null) ? petBase.getHp()     : userPet.getHp();
        int baseMana = (petBase != null) ? petBase.getMana()   : userPet.getMana();

        int step = level - 1;

        // Scale cơ bản
        double atk  = baseAtk  * Math.pow(ATK_GROWTH_PER_LEVEL, step);
        double hp   = baseHp   * Math.pow(HP_GROWTH_PER_LEVEL, step);
        double mana = baseMana * Math.pow(MANA_GROWTH_PER_LEVEL, step);

        // Buff mốc 7
        if (level >= MILESTONE_LV1) {
            atk  *= MILESTONE_LV1_BONUS;
            hp   *= MILESTONE_LV1_BONUS;
            mana *= MILESTONE_LV1_BONUS;
        }

        // Buff mốc 10
        if (level >= MILESTONE_LV2) {
            atk  *= MILESTONE_LV2_BONUS;
            hp   *= MILESTONE_LV2_BONUS;
            mana *= MILESTONE_LV2_BONUS;
        }

        // Weakness: ưu tiên UserPetDTO, nếu null thì lấy từ PetDTO, cuối cùng default = 1
        BigDecimal weakness =
                (userPet.getWeaknessValue() != null && userPet.getWeaknessValue().compareTo(BigDecimal.ZERO) > 0)
                        ? userPet.getWeaknessValue()
                        : (petBase != null && petBase.getWeaknessValue() != null
                        ? petBase.getWeaknessValue()
                        : BigDecimal.ONE);

        // Gán lại vào UserPetDTO
        userPet.setLevel(level);
        userPet.setMaxLevel(maxLevel);
        userPet.setAttack((int) Math.round(atk));
        userPet.setHp((int) Math.round(hp));
        userPet.setMana((int) Math.round(mana));
        userPet.setWeaknessValue(weakness);

        // Fill thêm info base nếu thiếu (cho đẹp DTO)
        if (petBase != null) {
            if (userPet.getPetId() == null) userPet.setPetId(petBase.getId());
            if (userPet.getName() == null) userPet.setName(petBase.getName());
            if (userPet.getElementType() == null) userPet.setElementType(petBase.getElementType());
            if (userPet.getElementOther() == null) userPet.setElementOther(petBase.getElementOther());
            if (userPet.getDes() == null) userPet.setDes(petBase.getDes());
        }

        return userPet;
    }

    /**
     * Helper: dùng khi chỉ có PetDTO + level (ví dụ tạo enemy, xem preview pet ở level X)
     * Trả về luôn UserPetDTO để thống nhất format.
     */
    public static UserPetDTO calculateFromPet(PetDTO petBase, int level) {
        if (petBase == null) return null;

        UserPetDTO dto = new UserPetDTO(
                null,        // Pet
                null,        // UserPet
                null,        // PetStats
                null,        // ElementWeakness
                null,        // User
                null         // SkillCard
        );

        dto.setPetId(petBase.getId());
        dto.setName(petBase.getName());
        dto.setElementType(petBase.getElementType());
        dto.setElementOther(petBase.getElementOther());
        dto.setMaxLevel(petBase.getMaxLevel());
        dto.setLevel(level);
        dto.setWeaknessValue(petBase.getWeaknessValue());

        return calculateStats(dto, petBase,0);
    }
}
