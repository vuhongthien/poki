package com.remake.poki.controller.admin;

import com.remake.poki.model.RechargePackage;
import com.remake.poki.request.CreateRechargePackageRequest;
import com.remake.poki.service.RechargePackageService;
import com.remake.poki.service.RechargeService;
import jakarta.servlet.http.HttpSession;
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

    private final RechargeService rechargeService;

    /**
     * API: Tạo gói hỗ trợ mới
     */
    @PostMapping
    public ResponseEntity<?> createPackage(@RequestBody CreateRechargePackageRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            RechargePackage newPackage = rechargePackageService.createPackage(request);

            response.put("success", true);
            response.put("message", "Tạo gói hỗ trợ thành công");
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
     * API: Lấy tất cả gói hỗ trợ (admin view)
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
     * API: Cập nhật gói hỗ trợ
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePackage(
            @PathVariable Long id,
            @RequestBody CreateRechargePackageRequest request) {

        Map<String, Object> response = new HashMap<>();

        try {
            RechargePackage updated = rechargePackageService.updatePackage(id, request);

            response.put("success", true);
            response.put("message", "Cập nhật gói hỗ trợ thành công");
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
     * API: Xóa gói hỗ trợ
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePackage(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            rechargePackageService.deletePackage(id);

            response.put("success", true);
            response.put("message", "Xóa gói hỗ trợ thành công");

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
     * API: Tạo các gói hỗ trợ mẫu (demo data)
     */
    @PostMapping("/seed")
    public ResponseEntity<?> seedPackages() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<RechargePackage> packages = rechargePackageService.createDemoPackages();

            response.put("success", true);
            response.put("message", "Tạo " + packages.size() + " gói hỗ trợ mẫu thành công");
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

    // ========== API ADMIN ==========

    /**
     * API ADMIN: Xác nhận thanh toán
     * POST /api/admin/recharge-packages/confirm/{transactionId}
     *
     * ĐÂY LÀ API QUAN TRỌNG NHẤT!
     * Admin gọi API này sau khi kiểm tra chuyển khoản thành công
     */
    @PostMapping("/confirm/{transactionId}")
    @ResponseBody
    public ResponseEntity<?> confirmPayment(@PathVariable String transactionId,
                                            HttpSession session) {
        // Kiểm tra quyền admin
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Bạn không có quyền thực hiện thao tác này!"
            ));
        }

        try {
            rechargeService.confirmPayment(transactionId);

            log.info("✅ Admin confirmed payment: {}", transactionId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đã xác nhận thanh toán thành công!"
            ));
        } catch (Exception e) {
            log.error("Error confirming payment", e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * API ADMIN: Lấy danh sách giao dịch PENDING (backward compatible)
     * GET /api/admin/recharge-packages/pending
     */
    @GetMapping("/pending")
    @ResponseBody
    public ResponseEntity<?> getPendingTransactions(HttpSession session) {
        // Kiểm tra quyền admin
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Bạn không có quyền truy cập!"
            ));
        }

        try {
            List<Map<String, Object>> transactions = rechargeService.getPendingTransactions();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "transactions", transactions,
                    "count", transactions.size()
            ));
        } catch (Exception e) {
            log.error("Error getting pending transactions", e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * API ADMIN: Lấy danh sách giao dịch theo STATUS với phân trang và tìm kiếm
     * GET /api/admin/recharge-packages/transactions
     * Params:
     *   - status: SUCCESS hoặc PENDING (required)
     *   - page: số trang (default 1)
     *   - size: số items/trang (default 20)
     *   - username: tìm kiếm theo username (optional)
     */
    @GetMapping("/transactions")
    @ResponseBody
    public ResponseEntity<?> getTransactionsByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String username,
            HttpSession session) {

        // Kiểm tra quyền admin
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Bạn không có quyền truy cập!"
            ));
        }

        // Validate status
        if (!status.equals("SUCCESS") && !status.equals("PENDING")) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Status phải là SUCCESS hoặc PENDING"
            ));
        }

        try {
            Map<String, Object> result = rechargeService.getTransactionsByStatusWithPagination(
                    status, page, size, username);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", result
            ));
        } catch (Exception e) {
            log.error("Error getting transactions by status", e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * API ADMIN: Lấy thống kê số lượng giao dịch và tổng tiền theo status
     * GET /api/admin/recharge-packages/stats
     * Params: status (SUCCESS hoặc PENDING)
     */
    @GetMapping("/stats")
    @ResponseBody
    public ResponseEntity<?> getTransactionStats(
            @RequestParam String status,
            HttpSession session) {

        // Kiểm tra quyền admin
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Bạn không có quyền truy cập!"
            ));
        }

        try {
            long count = rechargeService.countTransactionsByStatus(status);
            long totalAmount = rechargeService.totalAmountByStatus(status);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "status", status,
                            "totalCount", count,
                            "totalAmount", totalAmount
                    )
            ));
        } catch (Exception e) {
            log.error("Error getting transaction stats", e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * API ADMIN: Lấy danh sách giao dịch SUCCESS (backward compatible)
     * GET /api/admin/recharge-packages/success
     */
    @GetMapping("/success")
    @ResponseBody
    public ResponseEntity<?> getSuccessTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String username,
            HttpSession session) {

        // Kiểm tra quyền admin
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Bạn không có quyền truy cập!"
            ));
        }

        try {
            Map<String, Object> result = rechargeService.getTransactionsByStatusWithPagination(
                    "SUCCESS", page, size, username);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", result
            ));
        } catch (Exception e) {
            log.error("Error getting success transactions", e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}