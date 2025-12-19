package com.remake.poki.service;

import com.remake.poki.enums.PackageStatus;
import com.remake.poki.enums.PackageType;
import com.remake.poki.model.RechargePackage;
import com.remake.poki.repo.RechargePackageRepository;
import com.remake.poki.request.CreateRechargePackageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RechargePackageService {

    private final RechargePackageRepository rechargePackageRepository;

    /**
     * Tạo gói nạp mới
     */
    @Transactional
    public RechargePackage createPackage(CreateRechargePackageRequest request) {
        RechargePackage pkg = new RechargePackage();

        pkg.setName(request.getName());
        pkg.setDescription(request.getDescription());
        pkg.setPackageType(request.getPackageType() != null ? request.getPackageType() : PackageType.NORMAL);
        pkg.setStatus(PackageStatus.ACTIVE);
        pkg.setPrice(request.getPrice());
        pkg.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);

        // Set rewards
        pkg.setGold(request.getGold() != null ? request.getGold() : 0);
        pkg.setRuby(request.getRuby());
        pkg.setEnergy(request.getEnergy());
        pkg.setExp(request.getExp());
        pkg.setStarWhite(request.getStarWhite());
        pkg.setStarBlue(request.getStarBlue());
        pkg.setStarRed(request.getStarRed());
        pkg.setWheel(request.getWheel());
        pkg.setWheelDay(request.getWheelDay());

        // Special rewards
        pkg.setPetId(request.getPetId());
        pkg.setCardId(request.getCardId());
        pkg.setAvtId(request.getAvtId());
        pkg.setStonesJson(request.getStonesJson());

        // Special flags
        pkg.setIsFirstTimePurchase(request.getIsFirstTimePurchase() != null ? request.getIsFirstTimePurchase() : false);
        pkg.setIsLimitedQuantity(request.getIsLimitedQuantity() != null ? request.getIsLimitedQuantity() : false);
        pkg.setMaxQuantity(request.getMaxQuantity());
        pkg.setBonusGoldPercent(request.getBonusGoldPercent());

        // Time
        pkg.setStartTime(request.getStartTime());
        pkg.setEndTime(request.getEndTime());

        pkg.setIconUrl(request.getIconUrl());

        return rechargePackageRepository.save(pkg);
    }

    /**
     * Lấy tất cả gói nạp
     */
    public List<RechargePackage> getAllPackages() {
        return rechargePackageRepository.findAll();
    }

    /**
     * Cập nhật gói nạp
     */
    @Transactional
    public RechargePackage updatePackage(Long id, CreateRechargePackageRequest request) {
        RechargePackage pkg = rechargePackageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy gói nạp ID: " + id));

        pkg.setName(request.getName());
        pkg.setDescription(request.getDescription());
        pkg.setPackageType(request.getPackageType());
        pkg.setPrice(request.getPrice());
        pkg.setSortOrder(request.getSortOrder());

        pkg.setGold(request.getGold());
        pkg.setRuby(request.getRuby());
        pkg.setEnergy(request.getEnergy());
        pkg.setExp(request.getExp());
        pkg.setStarWhite(request.getStarWhite());
        pkg.setStarBlue(request.getStarBlue());
        pkg.setStarRed(request.getStarRed());
        pkg.setWheel(request.getWheel());

        pkg.setPetId(request.getPetId());
        pkg.setCardId(request.getCardId());
        pkg.setAvtId(request.getAvtId());
        pkg.setStonesJson(request.getStonesJson());

        pkg.setIsFirstTimePurchase(request.getIsFirstTimePurchase());
        pkg.setIsLimitedQuantity(request.getIsLimitedQuantity());
        pkg.setMaxQuantity(request.getMaxQuantity());
        pkg.setBonusGoldPercent(request.getBonusGoldPercent());

        pkg.setStartTime(request.getStartTime());
        pkg.setEndTime(request.getEndTime());
        pkg.setIconUrl(request.getIconUrl());

        return rechargePackageRepository.save(pkg);
    }

    /**
     * Xóa gói nạp
     */
    @Transactional
    public void deletePackage(Long id) {
        if (!rechargePackageRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy gói nạp ID: " + id);
        }
        rechargePackageRepository.deleteById(id);
    }

    /**
     * Tạo các gói nạp mẫu (demo data)
     */
    @Transactional
    public List<RechargePackage> createDemoPackages() {
        List<RechargePackage> packages = new ArrayList<>();

        // Gói Tân Thủ Đặc Biệt
        RechargePackage firstTime = new RechargePackage();
        firstTime.setName("Gói Tân Thủ Đặc Biệt");
        firstTime.setDescription("Gói nạp lần đầu siêu ưu đãi: x2 Gold + Thú cưng hiếm + 5 đá mỗi loại! Chỉ mua 1 lần duy nhất!");
        firstTime.setPackageType(PackageType.FIRST_TIME);
        firstTime.setStatus(PackageStatus.ACTIVE);
        firstTime.setPrice(50000);
        firstTime.setSortOrder(0);
        firstTime.setGold(100000);
        firstTime.setRuby(100);
        firstTime.setEnergy(50);
        firstTime.setExp(1000);
        firstTime.setStarWhite(10);
        firstTime.setStarBlue(5);
        firstTime.setStarRed(2);
        firstTime.setWheel(3);
        firstTime.setPetId(1L);
        firstTime.setStonesJson("[{\"stoneId\":1,\"count\":5},{\"stoneId\":2,\"count\":5},{\"stoneId\":3,\"count\":5},{\"stoneId\":4,\"count\":5},{\"stoneId\":5,\"count\":5}]");
        firstTime.setIsFirstTimePurchase(true);
        firstTime.setBonusGoldPercent(100);
        firstTime.setIconUrl("https://example.com/icons/first_time_package.png");
        packages.add(rechargePackageRepository.save(firstTime));

        // Gói Khởi Đầu - 20k
        RechargePackage starter = new RechargePackage();
        starter.setName("Gói Khởi Đầu");
        starter.setDescription("Gói nạp nhỏ để bắt đầu hành trình");
        starter.setPackageType(PackageType.NORMAL);
        starter.setStatus(PackageStatus.ACTIVE);
        starter.setPrice(20000);
        starter.setSortOrder(1);
        starter.setGold(20000);
        starter.setRuby(20);
        starter.setEnergy(20);
        starter.setExp(200);
        starter.setStarWhite(5);
        starter.setWheel(1);
        starter.setIsFirstTimePurchase(false);
        starter.setIconUrl("https://example.com/icons/package_20k.png");
        packages.add(rechargePackageRepository.save(starter));

        // Gói Tiêu Chuẩn - 50k
        RechargePackage standard = new RechargePackage();
        standard.setName("Gói Tiêu Chuẩn");
        standard.setDescription("Gói phổ biến cho người chơi");
        standard.setPackageType(PackageType.NORMAL);
        standard.setStatus(PackageStatus.ACTIVE);
        standard.setPrice(50000);
        standard.setSortOrder(2);
        standard.setGold(50000);
        standard.setRuby(60);
        standard.setEnergy(50);
        standard.setExp(500);
        standard.setStarWhite(10);
        standard.setStarBlue(3);
        standard.setWheel(2);
        standard.setIsFirstTimePurchase(false);
        standard.setIconUrl("https://example.com/icons/package_50k.png");
        packages.add(rechargePackageRepository.save(standard));

        // Gói Phổ Thông - 100k
        RechargePackage common = new RechargePackage();
        common.setName("Gói Phổ Thông");
        common.setDescription("Gói nạp được ưa chuộng");
        common.setPackageType(PackageType.NORMAL);
        common.setStatus(PackageStatus.ACTIVE);
        common.setPrice(100000);
        common.setSortOrder(3);
        common.setGold(100000);
        common.setRuby(150);
        common.setEnergy(100);
        common.setExp(1000);
        common.setStarWhite(15);
        common.setStarBlue(8);
        common.setStarRed(2);
        common.setWheel(5);
        common.setIsFirstTimePurchase(false);
        common.setIconUrl("https://example.com/icons/package_100k.png");
        packages.add(rechargePackageRepository.save(common));

        RechargePackage vip = new RechargePackage();
        vip.setName("Gói VIP");
        vip.setDescription("Gói nạp cao cấp cho player");
        vip.setPackageType(PackageType.NORMAL);
        vip.setStatus(PackageStatus.ACTIVE);
        vip.setPrice(500000);
        vip.setSortOrder(4);
        vip.setGold(500000);
        vip.setRuby(1000);
        vip.setEnergy(500);
        vip.setExp(5000);
        vip.setStarWhite(50);
        vip.setStarBlue(25);
        vip.setStarRed(10);
        vip.setWheel(20);
        vip.setPetId(1L);
        vip.setIsFirstTimePurchase(false);
        vip.setIconUrl("https://example.com/icons/package_500k.png");
        packages.add(rechargePackageRepository.save(vip));

        // Gói Đế Vương - 1000k
        RechargePackage emperor = new RechargePackage();
        emperor.setName("Gói Đế Vương");
        emperor.setDescription("Gói nạp tối thượng dành cho bậc đế vương");
        emperor.setPackageType(PackageType.NORMAL);
        emperor.setStatus(PackageStatus.ACTIVE);
        emperor.setPrice(1000000);
        emperor.setSortOrder(5);
        emperor.setGold(1000000);
        emperor.setRuby(2500);
        emperor.setEnergy(1000);
        emperor.setExp(10000);
        emperor.setStarWhite(100);
        emperor.setStarBlue(50);
        emperor.setStarRed(25);
        emperor.setWheel(50);
        emperor.setPetId(1L);
        emperor.setIsFirstTimePurchase(false);
        emperor.setIconUrl("https://example.com/icons/package_1000k.png");
        packages.add(rechargePackageRepository.save(emperor));

        return packages;
    }
}