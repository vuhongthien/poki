package com.remake.poki.controller.admin;

import com.remake.poki.model.RechargePackage;
import com.remake.poki.request.CreateRechargePackageRequest;
import com.remake.poki.service.RechargePackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/recharge-packages")
@RequiredArgsConstructor
@Slf4j
public class AdminRechargeController {

    private final RechargePackageService rechargePackageService;

    /**
     * API: Tạo gói nạp mới
     */
    @PostMapping
    public ResponseEntity<?> createPackage(@RequestBody CreateRechargePackageRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            RechargePackage newPackage = rechargePackageService.createPackage(request);

            response.put("success", true);
            response.put("message", "Tạo gói nạp thành công");
            response.put("package", newPackage);

            log.info("Created recharge package: {}", newPackage.getName());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error creating recharge package", e);
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Lấy tất cả gói nạp (admin view)
     */
    @GetMapping
    public ResponseEntity<?> getAllPackages() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<RechargePackage> packages = rechargePackageService.getAllPackages();

            response.put("success", true);
            response.put("packages", packages);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting packages", e);
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Cập nhật gói nạp
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePackage(
            @PathVariable Long id,
            @RequestBody CreateRechargePackageRequest request) {

        Map<String, Object> response = new HashMap<>();

        try {
            RechargePackage updated = rechargePackageService.updatePackage(id, request);

            response.put("success", true);
            response.put("message", "Cập nhật gói nạp thành công");
            response.put("package", updated);

            log.info("Updated recharge package: {}", updated.getName());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error updating recharge package", e);
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Xóa gói nạp
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePackage(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            rechargePackageService.deletePackage(id);

            response.put("success", true);
            response.put("message", "Xóa gói nạp thành công");

            log.info("Deleted recharge package ID: {}", id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error deleting recharge package", e);
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Tạo các gói nạp mẫu (demo data)
     */
    @PostMapping("/seed")
    public ResponseEntity<?> seedPackages() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<RechargePackage> packages = rechargePackageService.createDemoPackages();

            response.put("success", true);
            response.put("message", "Tạo " + packages.size() + " gói nạp mẫu thành công");
            response.put("packages", packages);

            log.info("Created {} demo packages", packages.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error seeding packages", e);
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}