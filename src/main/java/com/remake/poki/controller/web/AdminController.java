package com.remake.poki.controller.web;

import com.remake.poki.model.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    /**
     * Trang đăng nhập admin
     * URL: /admin/login
     */
    @GetMapping("/login")
    public String showAdminLogin(Model model, HttpSession session) {
        // Nếu đã đăng nhập admin rồi, chuyển thẳng đến trang admin
        if (isAdmin(session)) {
            return "redirect:/admin/recharge";
        }
        
        model.addAttribute("gameName", "Pokiguard");
        return "admin-login";
    }

    /**
     * Xử lý đăng nhập admin
     * POST: /admin/login
     */
    @PostMapping("/login")
    public String handleAdminLogin(@RequestParam String username,
                                   @RequestParam String password,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        
        // ⚠️ THAY ĐỔI USERNAME VÀ PASSWORD ADMIN CỦA BẠN Ở ĐÂY
        String ADMIN_USERNAME = "adminpoki";
        String ADMIN_PASSWORD = "adminpoki";
        
        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            session.setAttribute("isAdmin", true);
            session.setAttribute("adminUsername", username);
            log.info("🔐 Admin logged in: {}", username);
            return "redirect:/admin/recharge";
        }
        
        redirectAttributes.addFlashAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
        return "redirect:/admin/login";
    }

    /**
     * Đăng xuất admin
     * GET: /admin/logout
     */
    @GetMapping("/logout")
    public String adminLogout(HttpSession session, RedirectAttributes redirectAttributes) {
        String adminUsername = (String) session.getAttribute("adminUsername");
        session.removeAttribute("isAdmin");
        session.removeAttribute("adminUsername");
        
        log.info("🔐 Admin logged out: {}", adminUsername);
        redirectAttributes.addFlashAttribute("success", "Đã đăng xuất thành công!");
        return "redirect:/admin/login";
    }

    /**
     * Trang quản lý hỗ trợ tiền
     * URL: /admin/recharge
     */
    @GetMapping("/recharge")
    public String showAdminRecharge(HttpSession session, RedirectAttributes redirectAttributes) {
        // Kiểm tra quyền admin
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập với quyền admin!");
            return "redirect:/admin/login";
        }
        
        log.info("📊 Admin viewing recharge management page");
        return "admin-recharge";
    }

    /**
     * Kiểm tra quyền admin
     */
    private boolean isAdmin(HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        return isAdmin != null && isAdmin;
    }
}
