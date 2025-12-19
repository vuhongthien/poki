package com.remake.poki.controller.web;

import com.remake.poki.dto.RechargePackageDTO;
import com.remake.poki.dto.RechargeMilestoneDTO;
import com.remake.poki.model.User;
import com.remake.poki.request.PurchasePackageRequest;
import com.remake.poki.response.PaymentResponse;
import com.remake.poki.service.MilestoneService;
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
    private final MilestoneService milestoneService;

    /**
     * Trang gói nạp
     */
    @GetMapping("/recharge")
    public String rechargePage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");

        // Chưa login thì redirect về login
        if (user == null) {
            return "redirect:/login";
        }

        // Load packages và milestones
        List<RechargePackageDTO> packages = rechargeService.getAllActivePackages(user.getId());
        List<RechargeMilestoneDTO> milestones = milestoneService.getAllMilestones(user.getId());
        Integer totalRecharge = rechargeService.getUserTotalRecharge(user.getId());

        model.addAttribute("packages", packages);
        model.addAttribute("milestones", milestones);
        model.addAttribute("totalRecharge", totalRecharge != null ? totalRecharge : 0);
        model.addAttribute("currentUser", user);

        return "recharge";
    }

    // ========== API ENDPOINTS cho AJAX calls ==========

    /**
     * API: Lấy gói nạp (cho AJAX)
     */
    @GetMapping("/api/recharge/packages")
    @ResponseBody
    public ResponseEntity<?> getPackages(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Vui lòng đăng nhập");
            return ResponseEntity.ok(response);
        }

        try {
            List<RechargePackageDTO> packages = rechargeService.getAllActivePackages(user.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("packages", packages);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error loading packages", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Mua gói nạp
     */
    @PostMapping("/api/recharge/purchase")
    @ResponseBody
    public ResponseEntity<?> purchasePackage(
            @RequestBody PurchasePackageRequest request,
            HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Vui lòng đăng nhập");
            return ResponseEntity.ok(response);
        }

        try {
            // Set userId from session
            request.setUserId(user.getId());

            PaymentResponse paymentResponse = rechargeService.purchasePackage(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("payment", paymentResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error purchasing package", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Lấy mốc nạp
     */
    @GetMapping("/api/recharge/milestones")
    @ResponseBody
    public ResponseEntity<?> getMilestones(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Vui lòng đăng nhập");
            return ResponseEntity.ok(response);
        }

        try {
            List<RechargeMilestoneDTO> milestones = milestoneService.getAllMilestones(user.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("milestones", milestones);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error loading milestones", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Nhận thưởng mốc nạp
     */
    @PostMapping("/api/recharge/milestones/{milestoneId}/claim")
    @ResponseBody
    public ResponseEntity<?> claimMilestone(
            @PathVariable Long milestoneId,
            HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Vui lòng đăng nhập");
            return ResponseEntity.ok(response);
        }

        try {
            RechargeMilestoneDTO milestone = milestoneService.claimMilestone(milestoneId, user.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("milestone", milestone);
            response.put("message", "Nhận thưởng thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error claiming milestone", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Lấy tổng nạp
     */
    @GetMapping("/api/recharge/total")
    @ResponseBody
    public ResponseEntity<?> getTotalRecharge(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Vui lòng đăng nhập");
            return ResponseEntity.ok(response);
        }

        try {
            Integer total = rechargeService.getUserTotalRecharge(user.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalAmount", total != null ? total : 0);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting total recharge", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Webhook xác nhận thanh toán (từ payment gateway)
     */
    @PostMapping("/api/recharge/confirm/{transactionId}")
    @ResponseBody
    public ResponseEntity<?> confirmPayment(@PathVariable String transactionId) {
        try {
            rechargeService.confirmPayment(transactionId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment confirmed");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error confirming payment", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}