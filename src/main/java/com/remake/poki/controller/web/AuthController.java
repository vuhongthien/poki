package com.remake.poki.controller.web;

import com.remake.poki.model.User;
import com.remake.poki.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    /**
     * API: Login
     */
    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = loginRequest.get("user");
            String password = loginRequest.get("password");

            if (username == null || username.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Tên đăng nhập không được để trống");
                return ResponseEntity.ok(response);
            }

            if (password == null || password.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Mật khẩu không được để trống");
                return ResponseEntity.ok(response);
            }

            // Tìm user theo username
            User user = userService.findByUser(username);

            if (user == null) {
                response.put("success", false);
                response.put("message", "Tài khoản không tồn tại");
                return ResponseEntity.ok(response);
            }

            // Kiểm tra password (nếu bạn có mã hóa, cần dùng password encoder)
            if (!user.getPassword().equals(password)) {
                response.put("success", false);
                response.put("message", "Mật khẩu không đúng");
                return ResponseEntity.ok(response);
            }

            // Lưu user vào session
            session.setAttribute("user", user);

            // Tạo user data để trả về (không bao gồm password)
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUser());
            userData.put("name", user.getName());
            userData.put("level", user.getLever());
            userData.put("gold", user.getGold());
            userData.put("ruby", user.getRuby());

            response.put("success", true);
            response.put("message", "Đăng nhập thành công");
            response.put("user", userData);

            log.info("User logged in: {}", username);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Login error", e);
            response.put("success", false);
            response.put("message", "Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Logout
     */
    @PostMapping("/api/logout")
    @ResponseBody
    public ResponseEntity<?> logout(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                log.info("User logged out: {}", user.getUser());
            }

            session.invalidate();

            response.put("success", true);
            response.put("message", "Đăng xuất thành công");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Logout error", e);
            response.put("success", false);
            response.put("message", "Lỗi đăng xuất: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Kiểm tra trạng thái đăng nhập
     */
    @GetMapping("/api/check-login")
    @ResponseBody
    public ResponseEntity<?> checkLogin(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        User user = (User) session.getAttribute("user");

        if (user != null) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUser());
            userData.put("name", user.getName());
            userData.put("level", user.getLever());
            userData.put("gold", user.getGold());
            userData.put("ruby", user.getRuby());

            response.put("success", true);
            response.put("user", userData);
        } else {
            response.put("success", false);
            response.put("message", "Not logged in");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Trang login (nếu cần)
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Nếu bạn có trang login riêng
    }

    /**
     * GET: Logout và redirect về trang chủ
     */
    @GetMapping("/logout")
    public String logoutPage(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                log.info("User logged out via GET: {}", user.getUser());
            }
            session.invalidate();
        } catch (Exception e) {
            log.error("Logout error", e);
        }

        return "redirect:/";
    }
}