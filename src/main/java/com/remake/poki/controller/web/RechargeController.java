package com.remake.poki.controller.web;

import com.remake.poki.dto.RechargePackageDTO;
import com.remake.poki.model.User;
import com.remake.poki.service.RechargeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RechargeController {

    private final RechargeService rechargeService;

    // ========== TRANG THANH TOÁN ==========

    /**
     * Hiển thị trang thanh toán khi user click "MUA NGAY"
     * URL: /payment/{packageId}
     */
    @GetMapping("/payment/{packageId}")
    public String showPaymentPage(@PathVariable Long packageId,
                                  Model model,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        // Kiểm tra đăng nhập
        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để tiếp tục!");
            return "redirect:/?tab=recharge";
        }

        try {
            // Lấy thông tin gói hỗ trợ
            RechargePackageDTO packageInfo = rechargeService.getPackageById(packageId, user.getId());

            if (packageInfo == null || !packageInfo.getIsAvailable()) {
                redirectAttributes.addFlashAttribute("error", "Gói hỗ trợ không khả dụng!");
                return "redirect:/?tab=recharge";
            }

            if (!packageInfo.getCanPurchase()) {
                redirectAttributes.addFlashAttribute("error", "Bạn đã mua gói này rồi!");
                return "redirect:/?tab=recharge";
            }

            // Tạo transaction PENDING
            String transactionId = rechargeService.createPendingTransaction(user.getId(), packageId);

            // ⚠️ CẬP NHẬT THÔNG TIN NGÂN HÀNG CỦA BẠN Ở ĐÂY
            String bankName = "Agribank";
            String accountNumber = "5908205318924";
            String accountName = "VU HONG THIEN";

            // Nội dung chuyển khoản: POKI {username} {packageId}
            String transferContent = "SUPORTPOKI " + user.getUser() + " " + packageId;

            // Thêm data vào model
            model.addAttribute("package", packageInfo);
            model.addAttribute("user", user);
            model.addAttribute("bankName", bankName);
            model.addAttribute("accountNumber", accountNumber);
            model.addAttribute("accountName", accountName);
            model.addAttribute("transferContent", transferContent);
            model.addAttribute("transactionId", transactionId);
            model.addAttribute("gameName", "Pokiguard");

            log.info("📄 User #{} ({}) viewing payment page for package #{}",
                    user.getId(), user.getUser(), packageId);

            return "payment";
        } catch (Exception e) {
            log.error("Error loading payment page", e);
            redirectAttributes.addFlashAttribute("error", "Đã có lỗi xảy ra: " + e.getMessage());
            return "redirect:/?tab=recharge";
        }
    }

    /**
     * Trang thông báo thanh toán thành công
     * URL: /payment/success
     */
    @GetMapping("/payment/success")
    public String paymentSuccess(Model model) {
        model.addAttribute("gameName", "Pokiguard");
        return "payment-success";
    }

    /**
     * Xử lý hủy thanh toán
     * URL: /payment/cancel
     */
    @GetMapping("/payment/cancel")
    public String paymentCancel(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("warning", "Bạn đã hủy thanh toán!");
        return "redirect:/?tab=recharge";
    }

    // ========== API ENDPOINTS cho AJAX ==========

    /**
     * API: Lấy danh sách gói hỗ trợ
     * GET /api/recharge/packages
     */
    @GetMapping("/api/recharge/packages")
    @ResponseBody
    public ResponseEntity<?> getPackages(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Vui lòng đăng nhập"
            ));
        }

        try {
            List<RechargePackageDTO> packages = rechargeService.getAllActivePackages(user.getId());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "packages", packages
            ));
        } catch (Exception e) {
            log.error("Error loading packages", e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * API: Lấy tổng hỗ trợ của user
     * GET /api/recharge/total
     */
    @GetMapping("/api/recharge/total")
    @ResponseBody
    public ResponseEntity<?> getTotalRecharge(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Vui lòng đăng nhập"
            ));
        }

        try {
            Integer total = rechargeService.getUserTotalRecharge(user.getId());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "totalAmount", total
            ));
        } catch (Exception e) {
            log.error("Error getting total recharge", e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }


}