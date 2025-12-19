package com.remake.poki.controller.web;

import com.remake.poki.dto.RechargePackageDTO;
import com.remake.poki.dto.RechargeMilestoneDTO;
import com.remake.poki.model.User;
import com.remake.poki.service.MilestoneService;
import com.remake.poki.service.RechargeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final RechargeService rechargeService;
    private final MilestoneService milestoneService;

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        // Game info
        String launchDate = "2025-12-25 00:00:00";
        String bannerImage = "banner.png";
        String leftPetGif = "pet-left.gif";
        String rightPetGif = "pet-right.gif";

        List<String> petImages = Arrays.asList(
                "pet1.png", "pet2.png", "pet3.png", "pet4.png",
                "pet5.png", "pet6.png", "pet7.png", "pet8.png"
        );

        model.addAttribute("launchDate", launchDate);
        model.addAttribute("gameName", "Pokiguard");
        model.addAttribute("gameDescription", "Hành trình thu phục và tiến hóa các sinh vật huyền thoại!");
        model.addAttribute("bannerImage", bannerImage);
        model.addAttribute("leftPetGif", leftPetGif);
        model.addAttribute("rightPetGif", rightPetGif);
        model.addAttribute("petImages", petImages);

        // Check login status
        User user = (User) session.getAttribute("user");
        boolean isLoggedIn = (user != null);
        model.addAttribute("isLoggedIn", isLoggedIn);

        if (isLoggedIn) {
            // User is logged in - load recharge data
            model.addAttribute("currentUser", user);

            try {
                // Load recharge packages
                List<RechargePackageDTO> packages = rechargeService.getAllActivePackages(user.getId());
                model.addAttribute("packages", packages);

                // Load milestones
                List<RechargeMilestoneDTO> milestones = milestoneService.getAllMilestones(user.getId());
                model.addAttribute("milestones", milestones);

                // Load total recharge
                Integer totalRecharge = rechargeService.getUserTotalRecharge(user.getId());
                model.addAttribute("totalRecharge", totalRecharge != null ? totalRecharge : 0);
            } catch (Exception e) {
                // If there's an error loading recharge data, use empty data
                model.addAttribute("packages", new ArrayList<>());
                model.addAttribute("milestones", new ArrayList<>());
                model.addAttribute("totalRecharge", 0);
            }
        } else {
            // Not logged in - provide empty data
            model.addAttribute("currentUser", null);
            model.addAttribute("packages", new ArrayList<>());
            model.addAttribute("milestones", new ArrayList<>());
            model.addAttribute("totalRecharge", 0);
        }

        return "index";
    }
}