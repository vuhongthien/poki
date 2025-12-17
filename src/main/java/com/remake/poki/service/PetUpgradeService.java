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
            if(user.getGold() < 5000){
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

            // Kiểm tra nếu đạt max level và có pet cha (không phải tự trỏ) thì tiến hóa
            if(userPet.getLevel() == pet.getMaxLevel()
                    && pet.getParentId() != 0
                    && pet.getParentId() != pet.getId()) { // ✅ Kiểm tra không phải tự trỏ

                userPet.setPetId((long) pet.getParentId());
                userPet.setLevel(pet.getMaxLevel());
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
                int newLevel = userPet.getLevel() - 1;

                // ✅ Tìm pet con (form trước đó) - Loại trừ trường hợp tự trỏ
                Pet previousPet = petRepository.findByParentId(Math.toIntExact(pet.getId()))
                        .stream()
                        .filter(p -> p.getId() != pet.getId()) // ✅ Loại trừ chính nó
                        .findFirst()
                        .orElse(null);

                if (previousPet != null && newLevel < previousPet.getMaxLevel()) {
                    // ✅ Thoái hóa về form trước
                    // VD: Form 2 đang level 7, giảm xuống 6 → Thoái hóa về Form 1 level 6
                    userPet.setPetId((long) previousPet.getId());
                    userPet.setLevel(newLevel);
                } else if (newLevel >= 1) {
                    // Giảm level bình thường (không cần thoái hóa)
                    userPet.setLevel(newLevel);
                } else {
                    // Không cho giảm xuống dưới level 1
                    userPet.setLevel(1);
                }

                userPetRepository.save(userPet);

                return new PetUpgradeResponse(
                        false,
                        "Nâng cấp thất bại! Pet đã bị giảm cấp.",
                        null
                );
            } else {
                // Nếu có bảo vệ → Không giảm cấp
                return new PetUpgradeResponse(
                        false,
                        "Nâng cấp thất bại! (Đã bảo vệ, không giảm cấp)",
                        null
                );
            }
        }
    }
}