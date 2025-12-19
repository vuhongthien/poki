package com.remake.poki.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.remake.poki.dto.*;
import com.remake.poki.enums.PackageStatus;
import com.remake.poki.enums.PackageType;
import com.remake.poki.model.*;
import com.remake.poki.repo.*;
import com.remake.poki.request.PurchasePackageRequest;
import com.remake.poki.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

    /**
     * Lấy tất cả gói nạp đang active
     */
    public List<RechargePackageDTO> getAllActivePackages(Long userId) {
        List<RechargePackage> packages = packageRepository.findByStatusOrderBySortOrder(PackageStatus.ACTIVE);
        
        return packages.stream()
                .map(pkg -> convertToDTO(pkg, userId))
                .collect(Collectors.toList());
    }

    /**
     * Lấy gói nạp lần đầu
     */
    public List<RechargePackageDTO> getFirstTimePackages(Long userId) {
        List<RechargePackage> packages = packageRepository
                .findByPackageTypeAndStatusOrderBySortOrder(PackageType.FIRST_TIME, PackageStatus.ACTIVE);
        
        return packages.stream()
                .map(pkg -> convertToDTO(pkg, userId))
                .collect(Collectors.toList());
    }

    /**
     * Lấy chi tiết 1 gói nạp
     */
    public RechargePackageDTO getPackageById(Long packageId, Long userId) {
        RechargePackage pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found: " + packageId));
        
        return convertToDTO(pkg, userId);
    }

    /**
     * Mua gói nạp
     */
    @Transactional
    public PaymentResponse purchasePackage(PurchasePackageRequest request) {
        Long userId = request.getUserId();
        Long packageId = request.getPackageId();

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

        // Create transaction
        String transactionId = "TXN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
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
        recharge.setPetReceived(pkg.getPetId());
        recharge.setCardReceived(pkg.getCardId());
        recharge.setStonesReceivedJson(pkg.getStonesJson());
        recharge.setTransactionId(transactionId);
        recharge.setPaymentMethod(request.getPaymentMethod());
        recharge.setStatus("PENDING");
        
        userRechargeRepository.save(recharge);

        // TODO: Integrate with real payment gateway (Momo, ZaloPay, etc.)
        // For now, return mock payment URL
        String paymentUrl = generatePaymentUrl(transactionId, pkg.getPrice(), request.getPaymentMethod());

        return PaymentResponse.builder()
                .transactionId(transactionId)
                .paymentUrl(paymentUrl)
                .amount(pkg.getPrice())
                .status("PENDING")
                .message("Redirect to payment gateway")
                .build();
    }

    /**
     * Xác nhận thanh toán thành công (webhook từ payment gateway)
     */
    @Transactional
    public void confirmPayment(String transactionId) {
        UserRecharge recharge = userRechargeRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if ("SUCCESS".equals(recharge.getStatus())) {
            log.warn("Transaction already completed: {}", transactionId);
            return;
        }

        // Update transaction status
        recharge.setStatus("SUCCESS");
        recharge.setCompletedAt(LocalDateTime.now());
        userRechargeRepository.save(recharge);

        // Add rewards to user
        User user = userRepository.findById(recharge.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (recharge.getGoldReceived() != null) {
            user.setGold(user.getGold() + recharge.getGoldReceived());
        }
        if (recharge.getRubyReceived() != null) {
            user.setRuby(user.getRuby() + recharge.getRubyReceived());
        }
        if (recharge.getEnergyReceived() != null) {
            user.setEnergy(user.getEnergy() + recharge.getEnergyReceived());
        }
        if (recharge.getExpReceived() != null) {
            user.setExp(user.getExp() + recharge.getExpReceived());
        }
        if (recharge.getStarWhiteReceived() != null) {
            user.setStarWhite(user.getStarWhite() + recharge.getStarWhiteReceived());
        }
        if (recharge.getStarBlueReceived() != null) {
            user.setStarBlue(user.getStarBlue() + recharge.getStarBlueReceived());
        }
        if (recharge.getStarRedReceived() != null) {
            user.setStarRed(user.getStarRed() + recharge.getStarRedReceived());
        }
        if (recharge.getWheelReceived() != null) {
            user.setWheel(user.getWheel() + recharge.getWheelReceived());
        }
        // TODO: Handle pet, card, stones rewards

        userRepository.save(user);

        // Update package purchase tracking
        if (recharge.getPackageId() != null) {
            RechargePackage pkg = packageRepository.findById(recharge.getPackageId())
                    .orElse(null);
            
            if (pkg != null) {
                // Increase sold count
                pkg.setSoldCount(pkg.getSoldCount() + 1);
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
                    } else {
                        purchase.setPurchaseCount(purchase.getPurchaseCount() + 1);
                    }
                    
                    userPackagePurchaseRepository.save(purchase);
                }
            }
        }

        log.info("✅ Payment confirmed: {} - User #{} received {} gold", 
                transactionId, user.getId(), recharge.getGoldReceived());
    }

    /**
     * Lấy lịch sử nạp của user
     */
    public List<UserRechargeDTO> getUserRechargeHistory(Long userId) {
        List<UserRecharge> recharges = userRechargeRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
        
        return recharges.stream()
                .map(this::convertToRechargeDTO)
                .collect(Collectors.toList());
    }

    /**
     * Tính tổng số tiền user đã nạp
     */
    public Integer getUserTotalRecharge(Long userId) {
        return userRechargeRepository.sumAmountByUserIdAndStatus(userId, "SUCCESS");
    }

    // === PRIVATE METHODS ===

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
                .petId(pkg.getPetId())
                .cardId(pkg.getCardId())
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
        if (stonesJson == null || stonesJson.isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            return objectMapper.readValue(stonesJson, new TypeReference<List<StoneRewardDTO>>() {});
        } catch (Exception e) {
            log.error("Error parsing stones JSON: {}", stonesJson, e);
            return new ArrayList<>();
        }
    }

    private String generatePaymentUrl(String transactionId, Integer amount, String paymentMethod) {
        // TODO: Integrate with real payment gateway
        // This is a mock implementation
        return String.format("https://payment.example.com/pay?txn=%s&amount=%d&method=%s", 
                transactionId, amount, paymentMethod);
    }
}
