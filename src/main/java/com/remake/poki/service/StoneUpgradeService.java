package com.remake.poki.service;

import com.remake.poki.dto.*;
import com.remake.poki.enums.ElementType;
import com.remake.poki.model.Stone;
import com.remake.poki.model.StoneUser;
import com.remake.poki.repo.StoneRepository;
import com.remake.poki.repo.StoneUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StoneUpgradeService {

    private final StoneUserRepository stoneUserRepository;
    private final StoneRepository stoneRepository;

    @Transactional
    public StoneUpgradeResponseDTO upgradeStone(StoneUpgradeRequestDTO request) {
        try {
            // 1. Validate input
            if (request.getStoneIds() == null || request.getStoneIds().length != 3) {
                return new StoneUpgradeResponseDTO(false, "Phải chọn đủ 3 viên đá!", null);
            }

            Long userId = request.getUserId();
            List<StoneUser> stoneUsers = new ArrayList<>();
            List<Stone> stones = new ArrayList<>();

            // 2. Load stone data
            for (Long stoneId : request.getStoneIds()) {
                Optional<StoneUser> stoneUserOpt = stoneUserRepository.findByIdUserAndIdStone(userId, stoneId);
                if (!stoneUserOpt.isPresent()) {
                    return new StoneUpgradeResponseDTO(false, "Không tìm thấy đá trong kho!", null);
                }

                StoneUser stoneUser = stoneUserOpt.get();
                if (stoneUser.getCount() <= 0) {
                    return new StoneUpgradeResponseDTO(false, "Không đủ đá để nâng cấp!", null);
                }

                stoneUsers.add(stoneUser);

                Optional<Stone> stoneOpt = stoneRepository.findById(stoneId);
                if (!stoneOpt.isPresent()) {
                    return new StoneUpgradeResponseDTO(false, "Đá không tồn tại!", null);
                }
                stones.add(stoneOpt.get());
            }

            // 3. Validate: Phải cùng level và cùng element
            int firstLevel = stones.get(0).getLever();
            ElementType firstElement = stones.get(0).getElementType();

            for (Stone stone : stones) {
                if (stone.getLever() != firstLevel) {
                    return new StoneUpgradeResponseDTO(false, "3 viên đá phải cùng level!", null);
                }
                if (stone.getElementType() != firstElement) {
                    return new StoneUpgradeResponseDTO(false, "3 viên đá phải cùng hệ!", null);
                }
            }

            // 4. Trừ 3 viên đá đã chọn
            for (StoneUser stoneUser : stoneUsers) {
                stoneUser.setCount(stoneUser.getCount() - 1);
                stoneUserRepository.save(stoneUser);
            }

            // 5. Xử lý kết quả nâng cấp
            if (request.isSuccess()) {
                // Thành công: Tìm hoặc tạo đá level cao hơn
                int nextLevel = firstLevel + 1;

                Optional<Stone> nextLevelStoneOpt = stoneRepository.findByElementTypeAndLever(firstElement, nextLevel);

                if (!nextLevelStoneOpt.isPresent()) {
                    return new StoneUpgradeResponseDTO(false, "Đá đã đạt level tối đa!", null);
                }

                Stone nextLevelStone = nextLevelStoneOpt.get();

                // Tìm hoặc tạo StoneUser cho đá level cao hơn
                Optional<StoneUser> nextStoneUserOpt = stoneUserRepository.findByIdUserAndIdStone(userId, nextLevelStone.getId());

                if (nextStoneUserOpt.isPresent()) {
                    // Đã có đá này, tăng số lượng
                    StoneUser nextStoneUser = nextStoneUserOpt.get();
                    nextStoneUser.setCount(nextStoneUser.getCount() + 1);
                    stoneUserRepository.save(nextStoneUser);
                } else {
                    // Chưa có đá này, tạo mới
                    StoneUser newStoneUser = new StoneUser();
                    newStoneUser.setIdUser(userId);
                    newStoneUser.setIdStone(nextLevelStone.getId());
                    newStoneUser.setCount(1);
                    stoneUserRepository.save(newStoneUser);
                }

                return new StoneUpgradeResponseDTO(true,
                        "Nâng cấp thành công! \nNhận được " + nextLevelStone.getName(),
                        nextLevel);
            } else {
                // Thất bại: Mất 3 viên đá
                return new StoneUpgradeResponseDTO(false,
                        "Nâng cấp thất bại!",
                        null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new StoneUpgradeResponseDTO(false, "Lỗi hệ thống: " + e.getMessage(), null);
        }
    }

    @Transactional
    public StoneBatchUpgradeResponseDTO batchUpgradeStones(StoneBatchUpgradeRequestDTO request) {
        try {

            int totalSuccess = 0;
            int totalFailed = 0;
            List<String> upgradeDetails = new ArrayList<>();

            // Lấy tất cả UserStone của user 1 lần
            List<StoneUser> userStones = stoneUserRepository.findByIdUser((long) request.getUserId());
            Map<Long, StoneUser> userStoneMap = new HashMap<>();
            for (StoneUser us : userStones) {
                userStoneMap.put(us.getIdStone(), us);
            }

            // Xử lý từng nhóm 3 viên đá
            for (StoneGroupDTO group : request.getStoneGroups()) {
                long stoneId = group.getStoneId();
                int quantity = group.getQuantity(); // Số viên đá (phải là 3)
                boolean success = group.isSuccess();

                if (quantity != 3) {
                    continue;
                }

                StoneUser userStone = userStoneMap.get(stoneId);
                if (userStone == null) {
                    totalFailed++;
                    continue;
                }

                // Kiểm tra đủ số lượng
                if (userStone.getCount() < 3) {
                    totalFailed++;
                    continue;
                }

                // Trừ 3 viên đá hiện tại
                userStone.setCount(userStone.getCount() - 3);

                if (success) {
                    // Thành công -> Cộng 1 viên đá level cao hơn
                    Optional<Stone> stoneOpt = stoneRepository.findById(userStone.getIdStone());
                    int nextLevel = stoneOpt.get().getLever() + 1;

                    // Tìm đá level cao hơn cùng element
                    Optional<Stone> nextStoneOpt = stoneRepository
                            .findByElementTypeAndLever(stoneOpt.get().getElementType(), nextLevel);

                    if (nextStoneOpt.isPresent()) {
                        Stone nextStone = nextStoneOpt.get();

                        // Kiểm tra user đã có đá level cao hơn chưa
                        StoneUser nextUserStone = userStoneMap.get(nextStone.getId());

                        if (nextUserStone == null) {
                            // Chưa có -> Tạo mới
                            nextUserStone = new StoneUser();
                            nextUserStone.setIdUser((long) request.getUserId());
                            nextUserStone.setIdStone(nextStone.getId());
                            nextUserStone.setCount(1);
                            stoneUserRepository.save(nextUserStone);
                            userStoneMap.put(nextStone.getId(), nextUserStone);
                        } else {
                            // Đã có -> Cộng thêm
                            nextUserStone.setCount(nextUserStone.getCount() + 1);
                        }

                        totalSuccess++;
                        upgradeDetails.add(String.format("✓ %s Lv%d → Lv%d",
                                stoneOpt.get().getName(), stoneOpt.get().getLever(), nextLevel));
                    } else {
                        totalFailed++;
                    }
                } else {
                    totalFailed++;
                    upgradeDetails.add(String.format("✗ %s Lv%d thất bại",
                            userStone.getIdStone(), 0));
                }
            }

            // Lưu tất cả thay đổi 1 lần
            stoneUserRepository.saveAll(userStoneMap.values());

            String message = String.format(
                    "Thành công: %d, Thất bại: %d",
                    totalSuccess, totalFailed
            );


            return new StoneBatchUpgradeResponseDTO(
                    true,
                    message,
                    totalSuccess,
                    totalFailed,
                    upgradeDetails
            );

        } catch (Exception e) {
            return new StoneBatchUpgradeResponseDTO(
                    false,
                    "Lỗi server: " + e.getMessage(),
                    0,
                    0,
                    new ArrayList<>()
            );
        }
    }
}