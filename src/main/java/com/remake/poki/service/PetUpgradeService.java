package com.remake.poki.service;
import com.remake.poki.dto.*;
import com.remake.poki.model.*;
import com.remake.poki.repo.*;
import com.remake.poki.request.PetUpgradeRequest;
import com.remake.poki.response.PetUpgradeResponse;
import com.remake.poki.util.Calculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetUpgradeService {

    private final UserPetRepository userPetRepository;
    private final StoneUserRepository stoneUserRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    @Transactional
    public PetUpgradeResponse upgradePet(PetUpgradeRequest request) {
        // 1. Validate UserPet
        UserPet userPet = userPetRepository.findById(request.getUserPetId())
                .orElseThrow(() -> new RuntimeException("Pet không tồn tại"));

        Pet pet = petRepository.findById(userPet.getPetId())
                .orElseThrow(() -> new RuntimeException("Pet data không tồn tại"));

        // Kiểm tra đã max level chưa
        if (userPet.getLevel() >= pet.getMaxLevel()) {
            return new PetUpgradeResponse(false, "Pet đã đạt level tối đa", null);
        }

        // 2. Lọc stoneIds không null
        List<Long> stoneIds = request.getStoneIds().stream()
                .filter(id -> id != null)
                .toList();

        if (stoneIds.isEmpty()) {
            return new PetUpgradeResponse(false, "Chưa chọn đá nâng cấp", null);
        }

        // 3. Kiểm tra và trừ đá
        for (Long stoneId : stoneIds) {
            StoneUser stoneUser = stoneUserRepository.findByIdUserAndIdStone(
                            userPet.getUserId(), stoneId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đá"));

            if (stoneUser.getCount() <= 0) {
                return new PetUpgradeResponse(false, "Không đủ đá để nâng cấp", null);
            }

            stoneUser.setCount(stoneUser.getCount() - 1);
            stoneUserRepository.save(stoneUser);
        }
        if (request.isPreventDowngrade()) {
            User user = userRepository.findById(userPet.getUserId()).orElseThrow();
            if(user.getGold()<5000){
                return new PetUpgradeResponse(
                        false,
                        "Nâng cấp thất bại! bạn đã hết gold",
                        null
                );
            }
            user.setGold(user.getGold() - 5000);
            userRepository.save(user);
        }

        // 4. ✅ Xử lý theo kết quả với preventDowngrade
        if (request.isSuccess()) {

            // Thành công: Tăng cấp
            userPet.setLevel(userPet.getLevel() + 1);

            // Kiểm tra nếu đạt max level và có pet cha thì tiến hóa
            if(userPet.getLevel() == pet.getMaxLevel() && pet.getParentId() != 0) {
                userPet.setPetId((long) pet.getParentId());
            }

            userPet = userPetRepository.save(userPet);

            UserPetDTO updatedPetDTO = Calculator.calculateStats(
                    userPetRepository.getUserPet(userPet.getUserId(), userPet.getPetId())
            );

            return new PetUpgradeResponse(
                    true,
                    "Nâng cấp thành công!",
                    updatedPetDTO
            );
        } else {
            // ✅ Thất bại: Kiểm tra preventDowngrade
            if (!request.isPreventDowngrade()) {
                // Nếu không bảo vệ → Giảm cấp
                if (userPet.getLevel() > 1) { // Đảm bảo không giảm xuống dưới level 1
                    userPet.setLevel(userPet.getLevel() - 1);
                    userPetRepository.save(userPet);
                }
                return new PetUpgradeResponse(
                        false,
                        "Nâng cấp thất bại!",
                        null
                );
            } else {
                // Nếu có bảo vệ → Không giảm cấp
                return new PetUpgradeResponse(
                        false,
                        "Nâng cấp thất bại!",
                        null
                );
            }
        }
    }
}