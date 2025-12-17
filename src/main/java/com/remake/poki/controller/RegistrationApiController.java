package com.remake.poki.controller;

import com.remake.poki.model.*;
import com.remake.poki.repo.*;
import com.remake.poki.request.RegisterRequest;
import com.remake.poki.response.RegisterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RegistrationApiController {

    private final UserRepository userRepository;
    private final UserPetRepository userPetRepository;
    private final UserAvatarRepository userAvatarRepository;
    private final StoneUserRepository stoneUserRepository;
    private final UserCardRepository userCardRepository;
    private final DeviceAccountRepository deviceAccountRepository;

    private static final int MAX_ACCOUNTS_PER_DEVICE = 3;

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        try {
            // 0. Validate device ID
            if (request.getDeviceId() == null || request.getDeviceId().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new RegisterResponse(false, "Device ID không hợp lệ!", null));
            }

            // 1. Check device account limit
            long accountCount = deviceAccountRepository.countByDeviceId(request.getDeviceId());

            if (accountCount >= MAX_ACCOUNTS_PER_DEVICE) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new RegisterResponse(false,
                                "Thiết bị này đã tạo đủ " + MAX_ACCOUNTS_PER_DEVICE + " tài khoản! Không thể tạo thêm.",
                                null));
            }

            // 2. Kiểm tra username đã tồn tại chưa
            if (userRepository.existsByUser(request.getUser())) {
                return ResponseEntity.badRequest()
                        .body(new RegisterResponse(false, "Tên đăng nhập đã tồn tại!", null));
            }

            // 2.5. Kiểm tra tên nhân vật đã tồn tại chưa
            if (userRepository.existsByName(request.getName())) {
                return ResponseEntity.badRequest()
                        .body(new RegisterResponse(false, "Tên nhân vật đã tồn tại! Vui lòng chọn tên khác.", null));
            }

            // 3. Tạo User mới với các giá trị mặc định
            User user = new User();
            user.setUser(request.getUser());
            user.setName(request.getName());
            user.setPassword(request.getPassword()); // Nên hash password trong thực tế

            // Set các giá trị mặc định
            user.setLever(1);
            user.setEnergy(80);
            user.setEnergyFull(80);
            user.setGold(100000);
            user.setRuby(0);
            user.setRequestAttack(1000);
            user.setWheel(3);
            user.setWheelDay(3);
            user.setStarWhite(0);
            user.setStarBlue(0);
            user.setStarRed(0);
            user.setPetId(1L); // Bunny Boy
            user.setAvtId(1L); // Avatar 1
            user.setExp(500);
            user.setExpCurrent(10);
            user.setLastEnergyUpdate(LocalDateTime.now());

            user = userRepository.save(user);

            // 4. Tạo 3 Pet mặc định (Bunny Boy, Gila, Hamma)
            createDefaultPet(user.getId(), 1L); // Bunny Boy lv1
            createDefaultPet(user.getId(), 4L); // Gila lv1
            createDefaultPet(user.getId(), 7L); // Latios lv1

            // 5. Tạo 3 Avatar mặc định
            createDefaultAvatar(user.getId(), 1L);
            createDefaultAvatar(user.getId(), 2L);
            createDefaultAvatar(user.getId(), 3L);

            // 6. Tạo 5 viên đá cấp 7 mỗi loại (Water, Fire, Earth, Metal, Wood)
            // Water Stones (ID: 1-7) -> Level 7 = ID 7
            createDefaultStone(user.getId(), 7L, 5); // Water Stone Lv7

            // Fire Stones (ID: 8-14) -> Level 7 = ID 14
            createDefaultStone(user.getId(), 14L, 5); // Fire Stone Lv7

            // Earth Stones (ID: 15-21) -> Level 7 = ID 21
            createDefaultStone(user.getId(), 21L, 5); // Earth Stone Lv7

            // Metal Stones (ID: 22-28) -> Level 7 = ID 28
            createDefaultStone(user.getId(), 28L, 5); // Metal Stone Lv7

            // Wood Stones (ID: 29-35) -> Level 7 = ID 35
            createDefaultStone(user.getId(), 35L, 5); // Wood Stone Lv7

            // 7. Tạo 3 Card mặc định (Card 1, 2, 3) mỗi card 99 lá
            createDefaultCard(user.getId(), 1L, 99);
            createDefaultCard(user.getId(), 2L, 99);
            createDefaultCard(user.getId(), 3L, 99);

            // 8. Save device account tracking
            DeviceAccount deviceAccount = new DeviceAccount();
            deviceAccount.setDeviceId(request.getDeviceId());
            deviceAccount.setUsername(request.getUser());
            deviceAccountRepository.save(deviceAccount);

            return ResponseEntity.ok(
                    new RegisterResponse(true, "Đăng ký thành công! Chào mừng đến với POKIGUARD!", user.getId())
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RegisterResponse(false, "Lỗi hệ thống: " + e.getMessage(), null));
        }
    }

    private void createDefaultPet(Long userId, Long petId) {
        UserPet userPet = new UserPet();
        userPet.setUserId(userId);
        userPet.setPetId(petId);
        userPet.setLevel(1);
        userPetRepository.save(userPet);
    }

    private void createDefaultAvatar(Long userId, Long avatarId) {
        UserAvatar userAvatar = new UserAvatar();
        userAvatar.setUserId(userId);
        userAvatar.setAvatarId(avatarId);
        userAvatar.setCreated(LocalDateTime.now());
        userAvatarRepository.save(userAvatar);
    }

    private void createDefaultStone(Long userId, Long stoneId, int count) {
        StoneUser stoneUser = new StoneUser();
        stoneUser.setIdUser(userId);
        stoneUser.setIdStone(stoneId);
        stoneUser.setCount(count);
        stoneUserRepository.save(stoneUser);
    }

    private void createDefaultCard(Long userId, Long cardId, int count) {
        UserCard userCard = new UserCard();
        userCard.setUserId(userId);
        userCard.setCardId(cardId);
        userCard.setCount(count);
        userCard.setLevel(1);
        userCardRepository.save(userCard);
    }

    @GetMapping("/check-device-limit")
    public ResponseEntity<Map<String, Object>> checkDeviceLimit(@RequestParam String deviceId) {
        Map<String, Object> response = new HashMap<>();

        try {
            long accountCount = deviceAccountRepository.countByDeviceId(deviceId);
            List<DeviceAccount> accounts = deviceAccountRepository.findByDeviceId(deviceId);

            response.put("success", true);
            response.put("accountCount", accountCount);
            response.put("maxAccounts", MAX_ACCOUNTS_PER_DEVICE);
            response.put("remainingSlots", MAX_ACCOUNTS_PER_DEVICE - accountCount);
            response.put("canCreate", accountCount < MAX_ACCOUNTS_PER_DEVICE);
            response.put("accounts", accounts);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi kiểm tra thiết bị: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/registered-accounts")
    public ResponseEntity<Map<String, Object>> getAllRegisteredAccounts() {
        Map<String, Object> response = new HashMap<>();

        try {

            LocalDateTime cutoffDate = LocalDateTime.of(2025, 12, 18, 5, 0, 0);
            List<User> allUsers = userRepository.findByCreatedAtAfterOrderByCreatedAtDesc(cutoffDate);

            List<Map<String, Object>> accounts = allUsers.stream()
                    .map(user -> {
                        Map<String, Object> acc = new HashMap<>();
                        acc.put("username", user.getUser());
                        acc.put("characterName", user.getName());
                        acc.put("level", user.getLever());
                        return acc;
                    })
                    .collect(java.util.stream.Collectors.toList());

            response.put("success", true);
            response.put("accounts", accounts);
            response.put("total", accounts.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lấy danh sách tài khoản: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}