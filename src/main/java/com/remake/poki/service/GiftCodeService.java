package com.remake.poki.service;

import com.remake.poki.enums.GiftStatus;
import com.remake.poki.enums.GiftType;
import com.remake.poki.model.*;
import com.remake.poki.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class GiftCodeService {

    @Autowired
    private GiftCodeRepository giftCodeRepository;

    @Autowired
    private UserGiftCodeRepository userGiftCodeRepository;

    @Autowired
    private GiftRepository giftRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public String redeemGiftCode(Long userId, String code) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

        GiftCode giftCode = giftCodeRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã gift code."));

        if (!giftCode.getIsActive()) {
            throw new RuntimeException("Mã gift code này đã bị vô hiệu hóa.");
        }

        if (giftCode.getExpiredAt() != null &&
                LocalDateTime.now().isAfter(giftCode.getExpiredAt())) {
            throw new RuntimeException("Mã gift code đã hết hạn.");
        }

        if (userGiftCodeRepository.existsByUserIdAndGiftCodeId(userId, giftCode.getId())) {
            throw new RuntimeException("Bạn đã sử dụng mã gift code này rồi.");
        }

        if (giftCode.getMaxUses() != null &&
                giftCode.getCurrentUses() >= giftCode.getMaxUses()) {
            throw new RuntimeException("Mã gift code đã đạt giới hạn số lần sử dụng.");
        }

        Gift gift = new Gift();
        gift.setUserId(userId);
        gift.setTitle(giftCode.getTitle());
        gift.setDescription(giftCode.getDescription());
        gift.setGiftType(GiftType.INDIVIDUAL);
        gift.setStatus(GiftStatus.PENDING);

        gift.setGold(giftCode.getGold());
        gift.setEnergy(giftCode.getEnergy());
        gift.setExp(giftCode.getExp());
        gift.setStarWhite(giftCode.getStarWhite());
        gift.setStarBlue(giftCode.getStarBlue());
        gift.setStarRed(giftCode.getStarRed());
        gift.setWheel(giftCode.getWheel());
        gift.setPetId(giftCode.getPetId());
        gift.setCardId(giftCode.getCardId());
        gift.setStonesJson(giftCode.getStonesJson());

        giftRepository.save(gift);

        UserGiftCode userGiftCode = new UserGiftCode();
        userGiftCode.setUserId(userId);
        userGiftCode.setGiftCodeId(giftCode.getId());
        userGiftCodeRepository.save(userGiftCode);

        giftCode.setCurrentUses(giftCode.getCurrentUses() + 1);
        giftCodeRepository.save(giftCode);

        return "Nhận gift code thành công! Vui lòng kiểm tra hộp quà của bạn.";
    }

}