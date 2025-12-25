package com.remake.poki.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.remake.poki.dto.HistoryUserRechargeDTO;
import com.remake.poki.dto.RechargePackageDTO;
import com.remake.poki.dto.StoneRewardDTO;
import com.remake.poki.dto.UserRechargeDTO;
import com.remake.poki.enums.PackageStatus;
import com.remake.poki.enums.PackageType;
import com.remake.poki.model.RechargePackage;
import com.remake.poki.model.User;
import com.remake.poki.model.UserPackagePurchase;
import com.remake.poki.model.UserRecharge;
import com.remake.poki.repo.RechargePackageRepository;
import com.remake.poki.repo.UserPackagePurchaseRepository;
import com.remake.poki.repo.UserRechargeRepository;
import com.remake.poki.repo.UserRepository;
import com.remake.poki.request.CreateGiftRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RechargeService {

    private final RechargePackageRepository packageRepository;
    private final UserRechargeRepository userRechargeRepository;
    private final UserPackagePurchaseRepository userPackagePurchaseRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    @Autowired
    RechargePackageRepository rechargePackageRepository;

    // ⭐ THÊM GiftService
    private final GiftService giftService;

    /**
     * Lấy tất cả gói hỗ trợ đang active
     */
    public List<RechargePackageDTO> getAllActivePackages(Long userId) {
        List<RechargePackage> packages = packageRepository.findByStatusOrderBySortOrder(PackageStatus.ACTIVE);

        return packages.stream()
                .filter(RechargePackage::isAvailable)
                .map(pkg -> convertToDTO(pkg, userId))
                .collect(Collectors.toList());
    }

    /**
     * Lấy gói hỗ trợ lần đầu
     */
    public List<RechargePackageDTO> getFirstTimePackages(Long userId) {
        List<RechargePackage> packages = packageRepository
                .findByPackageTypeAndStatusOrderBySortOrder(PackageType.FIRST_TIME, PackageStatus.ACTIVE);

        return packages.stream()
                .filter(RechargePackage::isAvailable)
                .map(pkg -> convertToDTO(pkg, userId))
                .collect(Collectors.toList());
    }

    /**
     * Lấy chi tiết 1 gói hỗ trợ - CHO TRANG PAYMENT
     */
    public RechargePackageDTO getPackageById(Long packageId, Long userId) {
        RechargePackage pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found: " + packageId));

        return convertToDTO(pkg, userId);
    }

    /**
     * Tạo transaction PENDING khi user nhấn "MUA NGAY"
     * Transaction này sẽ được confirm sau khi admin xác nhận chuyển khoản
     */
    @Transactional
    public String createPendingTransaction(Long userId, Long packageId) {
        // Validate package
        RechargePackage pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found"));

        if (!pkg.isAvailable()) {
            throw new RuntimeException("Package is not available");
        }

        // Check first time purchase
        if (pkg.getIsFirstTimePurchase()) {
            boolean alreadyPurchased = userPackagePurchaseRepository
                    .existsByUserIdAndPackageId(userId, packageId);

            if (alreadyPurchased) {
                throw new RuntimeException("You can only purchase this package once");
            }
        }

        // Check limited quantity
        if (pkg.getIsLimitedQuantity() && pkg.getMaxQuantity() != null) {
            if (pkg.getSoldCount() >= pkg.getMaxQuantity()) {
                throw new RuntimeException("Package sold out");
            }
        }

        // Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create transaction với status PENDING
        String transactionId = "TXN_" + System.currentTimeMillis() + "_" + userId;

        UserRecharge recharge = new UserRecharge();
        recharge.setUserId(userId);
        recharge.setPackageId(packageId);
        recharge.setAmount(pkg.getPrice());
        recharge.setGoldReceived(pkg.calculateTotalGold());
        recharge.setRubyReceived(pkg.getRuby());
        recharge.setEnergyReceived(pkg.getEnergy());
        recharge.setExpReceived(pkg.getExp());
        recharge.setStarWhiteReceived(pkg.getStarWhite());
        recharge.setStarBlueReceived(pkg.getStarBlue());
        recharge.setStarRedReceived(pkg.getStarRed());
        recharge.setWheelReceived(pkg.getWheel());
        recharge.setWheelDayReceived(pkg.getWheelDay());
        recharge.setAvtReceived(pkg.getAvtId());
        recharge.setPetReceived(pkg.getPetId());
        recharge.setCardReceived(pkg.getCardId());
        recharge.setStonesReceivedJson(pkg.getStonesJson());
        recharge.setTransactionId(transactionId);
        recharge.setPaymentMethod("BANK_TRANSFER");
        recharge.setStatus("PENDING");
        recharge.setNote("Waiting for bank transfer confirmation");

        userRechargeRepository.save(recharge);

        log.info("✅ Created PENDING transaction: {} for user #{}", transactionId, userId);
        return transactionId;
    }

    /**
     * ⭐⭐⭐ XÁC NHẬN THANH TOÁN - ADMIN GỌI API NÀY SAU KHI CHECK NGÂN HÀNG
     * TÍCH HỢP GIFT SERVICE - TẠO GIFT THAY VÌ CỘNG TRỰC TIẾP VÀO USER
     */
    @Transactional
    public void confirmPayment(String transactionId) {
        UserRecharge recharge = userRechargeRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));

        if ("SUCCESS".equals(recharge.getStatus())) {
            log.warn("Transaction already completed: {}", transactionId);
            return;
        }

        // Update transaction status
        recharge.setStatus("SUCCESS");
        recharge.setCompletedAt(LocalDateTime.now());
        userRechargeRepository.save(recharge);

        // Validate user
        User user = userRepository.findById(recharge.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ⭐⭐⭐ TẠO GIFT THAY VÌ CỘNG TRỰC TIẾP
        createRechargeGift(recharge, user);

        // Update package tracking
        if (recharge.getPackageId() != null) {
            RechargePackage pkg = packageRepository.findById(recharge.getPackageId())
                    .orElse(null);

            if (pkg != null) {
                // Tăng số lượng đã bán
                pkg.setSoldCount((pkg.getSoldCount() != null ? pkg.getSoldCount() : 0) + 1);
                packageRepository.save(pkg);

                // Track first time purchase
                if (pkg.getIsFirstTimePurchase()) {
                    UserPackagePurchase purchase = userPackagePurchaseRepository
                            .findByUserIdAndPackageId(user.getId(), pkg.getId())
                            .orElse(new UserPackagePurchase());

                    if (purchase.getId() == null) {
                        purchase.setUserId(user.getId());
                        purchase.setPackageId(pkg.getId());
                        purchase.setPurchaseCount(1);
                        purchase.setFirstPurchaseAt(LocalDateTime.now());
                    } else {
                        purchase.setPurchaseCount(purchase.getPurchaseCount() + 1);
                    }
                    purchase.setLastPurchaseAt(LocalDateTime.now());

                    userPackagePurchaseRepository.save(purchase);
                }
            }
        }

        log.info("✅ Payment confirmed successfully: {} - Gift created for user #{}",
                transactionId, user.getId());
    }

    /**
     * ⭐⭐⭐ TẠO GIFT KHI CONFIRM THANH TOÁN
     * Gift này sẽ được user nhận từ hệ thống Gift
     */
    private void createRechargeGift(UserRecharge recharge, User user) {
        try {
            // Lấy thông tin package
            String packageName = "Gói hỗ trợ";
            if (recharge.getPackageId() != null) {
                packageName = packageRepository.findById(recharge.getPackageId())
                        .map(RechargePackage::getName)
                        .orElse("Gói hỗ trợ");
            }

            // Tạo Gift Request
            CreateGiftRequest giftRequest = new CreateGiftRequest();
            giftRequest.setUserId(user.getId());
            giftRequest.setTitle("🎁 " + packageName);

            // Build description
            StringBuilder description = new StringBuilder();
            description.append("Cảm ơn bạn đã hỗ trợ tiền!\n");
            description.append("Mã giao dịch: ").append(recharge.getTransactionId()).append("\n\n");
            description.append("Phần thưởng:\n");

            // Gold
            if (recharge.getGoldReceived() != null && recharge.getGoldReceived() > 0) {
                giftRequest.setGold(recharge.getGoldReceived());
                description.append("• ").append(formatNumber(recharge.getGoldReceived())).append(" Gold\n");
            }

            // Ruby
            if (recharge.getRubyReceived() != null && recharge.getRubyReceived() > 0) {
                giftRequest.setRuby(recharge.getRubyReceived());
                description.append("• ").append(formatNumber(recharge.getRubyReceived())).append(" Ruby\n");
            }

            // Energy
            if (recharge.getEnergyReceived() != null && recharge.getEnergyReceived() > 0) {
                giftRequest.setEnergy(recharge.getEnergyReceived());
                description.append("• ").append(formatNumber(recharge.getEnergyReceived())).append(" Energy\n");
            }

            // EXP
            if (recharge.getExpReceived() != null && recharge.getExpReceived() > 0) {
                giftRequest.setExp(recharge.getExpReceived());
                description.append("• ").append(formatNumber(recharge.getExpReceived())).append(" EXP\n");
            }

            // Star White
            if (recharge.getStarWhiteReceived() != null && recharge.getStarWhiteReceived() > 0) {
                giftRequest.setStarWhite(recharge.getStarWhiteReceived());
                description.append("• ").append(formatNumber(recharge.getStarWhiteReceived())).append(" Sao trắng\n");
            }

            // Star Blue
            if (recharge.getStarBlueReceived() != null && recharge.getStarBlueReceived() > 0) {
                giftRequest.setStarBlue(recharge.getStarBlueReceived());
                description.append("• ").append(formatNumber(recharge.getStarBlueReceived())).append(" Sao xanh\n");
            }

            // Star Red
            if (recharge.getStarRedReceived() != null && recharge.getStarRedReceived() > 0) {
                giftRequest.setStarRed(recharge.getStarRedReceived());
                description.append("• ").append(formatNumber(recharge.getStarRedReceived())).append(" Sao đỏ\n");
            }

            // Wheel
            if (recharge.getWheelReceived() != null && recharge.getWheelReceived() > 0) {
                giftRequest.setWheel(recharge.getWheelReceived());
                description.append("• ").append(formatNumber(recharge.getWheelReceived())).append(" Vòng quay huyền thoại\n");
            }

            // Wheel Day
            if (recharge.getWheelDayReceived() != null && recharge.getWheelDayReceived() > 0) {
                giftRequest.setWheelDay(recharge.getWheelDayReceived());
                description.append("• ").append(formatNumber(recharge.getWheelDayReceived())).append(" Vòng quay hàng ngày\n");
            }

            // Pet
            if (recharge.getPetReceived() != null) {
                giftRequest.setPetId(recharge.getPetReceived());
                description.append("• 1 Pet đặc biệt\n");
            }


            // avt
            if (recharge.getAvtReceived() != null) {
                giftRequest.setAvtId(recharge.getAvtReceived());
                description.append("• 1 Avatar \n");
            }

            // Card
            if (recharge.getCardReceived() != null) {
                giftRequest.setCardId(recharge.getCardReceived());
                description.append("• 1 Thẻ chiến đấu đặc biệt\n");
            }

            // Stones
            if (recharge.getStonesReceivedJson() != null && !recharge.getStonesReceivedJson().isEmpty()) {
                List<StoneRewardDTO> stones = parseStones(recharge.getStonesReceivedJson());
                if (!stones.isEmpty()) {
                    giftRequest.setStones(stones.stream()
                            .map(s -> {
                                com.remake.poki.dto.StoneReward sr = new com.remake.poki.dto.StoneReward();
                                sr.setStoneId(s.getStoneId());
                                sr.setCount(s.getCount());
                                return sr;
                            })
                            .collect(Collectors.toList()));

                    description.append("• Đá tiến hóa các loại\n");
                }
            }

            giftRequest.setDescription(description.toString());

            // Gift hết hạn sau 30 ngày
            giftRequest.setExpiredAt(LocalDateTime.now().plusDays(30));

            // ⭐ GỬI GIFT QUA GiftService
            giftService.sendGiftToUser(giftRequest);

            log.info("🎁 Created recharge gift for user #{} from transaction {}",
                    user.getId(), recharge.getTransactionId());

        } catch (Exception e) {
            log.error("❌ Failed to create recharge gift for user #{}: {}",
                    user.getId(), e.getMessage(), e);
            // Không throw exception để không làm fail transaction
            // Gift sẽ được tạo lại bằng cách khác nếu cần
        }
    }

    /**
     * Lấy danh sách giao dịch PENDING (cho admin)
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPendingTransactions() {
        long startTime = System.currentTimeMillis();

        // Query 1: Lấy tất cả pending transactions
        List<UserRecharge> pending = userRechargeRepository.findByStatusOrderByCreatedAtDesc("PENDING");

        if (pending.isEmpty()) {
            log.info("✅ No pending transactions found");
            return Collections.emptyList();
        }

        log.info("📊 Found {} pending transactions", pending.size());

        // Collect unique userIds và packageIds
        Set<Long> userIds = pending.stream()
                .map(UserRecharge::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> packageIds = pending.stream()
                .map(UserRecharge::getPackageId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Query 2: Batch load tất cả users (1 query thay vì N queries)
        Map<Long, User> usersMap = Collections.emptyMap();
        if (!userIds.isEmpty()) {
            usersMap = userRepository.findAllById(userIds).stream()
                    .collect(Collectors.toMap(User::getId, user -> user));
            log.debug("📥 Loaded {} users in batch", usersMap.size());
        }

        // Query 3: Batch load tất cả packages (1 query thay vì N queries)
        Map<Long, RechargePackage> packagesMap = Collections.emptyMap();
        if (!packageIds.isEmpty()) {
            packagesMap = packageRepository.findAllById(packageIds).stream()
                    .collect(Collectors.toMap(RechargePackage::getId, pkg -> pkg));
            log.debug("📥 Loaded {} packages in batch", packagesMap.size());
        }

        // Map final để tránh effectively final issue
        final Map<Long, User> finalUsersMap = usersMap;
        final Map<Long, RechargePackage> finalPackagesMap = packagesMap;

        // Build response - Chỉ lookup từ Map, không query thêm
        List<Map<String, Object>> result = pending.stream()
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();

                    // Basic info
                    map.put("id", r.getId());
                    map.put("transactionId", r.getTransactionId());
                    map.put("userId", r.getUserId());
                    map.put("packageId", r.getPackageId());
                    map.put("amount", r.getAmount());
                    map.put("goldReceived", r.getGoldReceived());
                    map.put("createdAt", r.getCreatedAt());
                    map.put("status", r.getStatus());

                    // User info - O(1) lookup từ HashMap
                    User user = finalUsersMap.get(r.getUserId());
                    if (user != null) {
                        map.put("username", user.getUser());
                        map.put("name", user.getName());
                    } else {
                        map.put("username", "Unknown");
                        map.put("name", "Unknown");
                    }

                    // Package info - O(1) lookup từ HashMap
                    RechargePackage pkg = finalPackagesMap.get(r.getPackageId());
                    if (pkg != null) {
                        map.put("packageName", pkg.getName());
                    } else {
                        map.put("packageName", "Unknown Package");
                    }

                    return map;
                })
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        log.info("✅ Loaded {} pending transactions in {}ms (3 queries total)",
                result.size(), executionTime);

        // Warning nếu query chậm
        if (executionTime > 500) {
            log.warn("⚠️ Query took longer than expected: {}ms. Consider adding indexes.",
                    executionTime);
        }

        return result;
    }

    /**
     * Lấy lịch sử hỗ trợ của user
     */
    public List<UserRechargeDTO> getUserRechargeHistory(Long userId) {
        List<UserRecharge> recharges = userRechargeRepository
                .findByUserIdOrderByCreatedAtDesc(userId);

        return recharges.stream()
                .map(this::convertToRechargeDTO)
                .collect(Collectors.toList());
    }

    /**
     * Tính tổng số tiền user đã hỗ trợ thành công
     */
    public Integer getUserTotalRecharge(Long userId) {
        Integer total = userRechargeRepository.sumAmountByUserIdAndStatus(userId, "SUCCESS");
        return total != null ? total : 0;
    }

    // === PRIVATE HELPER METHODS ===

    private RechargePackageDTO convertToDTO(RechargePackage pkg, Long userId) {
        List<StoneRewardDTO> stones = parseStones(pkg.getStonesJson());

        boolean canPurchase = true;
        if (pkg.getIsFirstTimePurchase()) {
            canPurchase = !userPackagePurchaseRepository
                    .existsByUserIdAndPackageId(userId, pkg.getId());
        }

        Integer remainingQuantity = null;
        if (pkg.getIsLimitedQuantity() && pkg.getMaxQuantity() != null) {
            remainingQuantity = pkg.getMaxQuantity() - pkg.getSoldCount();
        }

        return RechargePackageDTO.builder()
                .id(pkg.getId())
                .name(pkg.getName())
                .description(pkg.getDescription())
                .packageType(pkg.getPackageType())
                .status(pkg.getStatus())
                .price(pkg.getPrice())
                .sortOrder(pkg.getSortOrder())
                .gold(pkg.getGold())
                .ruby(pkg.getRuby())
                .energy(pkg.getEnergy())
                .exp(pkg.getExp())
                .starWhite(pkg.getStarWhite())
                .starBlue(pkg.getStarBlue())
                .starRed(pkg.getStarRed())
                .wheel(pkg.getWheel())
                .wheelDay(pkg.getWheelDay())
                .petId(pkg.getPetId())
                .cardId(pkg.getCardId())
                .avtId(pkg.getAvtId())
                .stones(stones)
                .isFirstTimePurchase(pkg.getIsFirstTimePurchase())
                .isLimitedQuantity(pkg.getIsLimitedQuantity())
                .maxQuantity(pkg.getMaxQuantity())
                .soldCount(pkg.getSoldCount())
                .remainingQuantity(remainingQuantity)
                .startTime(pkg.getStartTime())
                .endTime(pkg.getEndTime())
                .bonusGoldPercent(pkg.getBonusGoldPercent())
                .totalGold(pkg.calculateTotalGold())
                .iconUrl(pkg.getIconUrl())
                .isAvailable(pkg.isAvailable())
                .canPurchase(canPurchase)
                .createdAt(pkg.getCreatedAt())
                .updatedAt(pkg.getUpdatedAt())
                .build();
    }

    private UserRechargeDTO convertToRechargeDTO(UserRecharge recharge) {
        String packageName = null;
        if (recharge.getPackageId() != null) {
            packageName = packageRepository.findById(recharge.getPackageId())
                    .map(RechargePackage::getName)
                    .orElse("Unknown Package");
        }

        List<StoneRewardDTO> stones = parseStones(recharge.getStonesReceivedJson());

        return UserRechargeDTO.builder()
                .id(recharge.getId())
                .userId(recharge.getUserId())
                .packageId(recharge.getPackageId())
                .packageName(packageName)
                .amount(recharge.getAmount())
                .goldReceived(recharge.getGoldReceived())
                .rubyReceived(recharge.getRubyReceived())
                .energyReceived(recharge.getEnergyReceived())
                .expReceived(recharge.getExpReceived())
                .starWhiteReceived(recharge.getStarWhiteReceived())
                .starBlueReceived(recharge.getStarBlueReceived())
                .starRedReceived(recharge.getStarRedReceived())
                .wheelReceived(recharge.getWheelReceived())
                .wheelDayReceived(recharge.getWheelDayReceived())
                .petReceived(recharge.getPetReceived())
                .cardReceived(recharge.getCardReceived())
                .stonesReceived(stones)
                .transactionId(recharge.getTransactionId())
                .paymentMethod(recharge.getPaymentMethod())
                .status(recharge.getStatus())
                .createdAt(recharge.getCreatedAt())
                .completedAt(recharge.getCompletedAt())
                .note(recharge.getNote())
                .build();
    }

    private List<StoneRewardDTO> parseStones(String stonesJson) {
        if (stonesJson == null || stonesJson.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(stonesJson, new TypeReference<List<StoneRewardDTO>>() {});
        } catch (Exception e) {
            log.error("Error parsing stones JSON: {}", stonesJson, e);
            return new ArrayList<>();
        }
    }

    private String formatNumber(Integer number) {
        if (number == null) return "0";
        return String.format("%,d", number);
    }

    public long totalAmountByStatus(String status) {
        return userRechargeRepository.totalAmountByStatus(status);
    }

    public List<HistoryUserRechargeDTO> listAmountUserByStatus(String userName) {
        User user = userRepository.findByUser(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserRecharge> userRechargeList =
                userRechargeRepository.listAmountUserByStatus("SUCCESS", user.getId());
        return userRechargeList.stream()
                .map(recharge -> HistoryUserRechargeDTO.builder()
                        .userId(recharge.getUserId())
                        .packageId(recharge.getPackageId())
                        .packageName(rechargePackageRepository.findById(recharge.getPackageId()).get().getName())
                        .amount(recharge.getAmount())
                        .createTime(recharge.getCreatedAt())
                        .build()
                )
                .toList();
    }

}