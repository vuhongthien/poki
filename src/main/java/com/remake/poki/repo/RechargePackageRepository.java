package com.remake.poki.repo;

import com.remake.poki.enums.PackageStatus;
import com.remake.poki.enums.PackageType;
import com.remake.poki.model.RechargePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RechargePackageRepository extends JpaRepository<RechargePackage, Long> {
    
    List<RechargePackage> findByStatusOrderBySortOrder(PackageStatus status);
    
    List<RechargePackage> findByPackageTypeAndStatusOrderBySortOrder(PackageType packageType, PackageStatus status);

    List<RechargePackage> findAllByOrderByIdDesc();
}
