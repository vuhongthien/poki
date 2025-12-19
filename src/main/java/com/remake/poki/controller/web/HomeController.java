package com.remake.poki.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Arrays;
import java.util.List;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        String launchDate = "2025-12-25 00:00:00";

        String bannerImage = "banner.png";

        String leftPetGif = "pet-left.gif";
        String rightPetGif = "pet-right.gif";

        List<String> petImages = Arrays.asList(
                "pet1.png",
                "pet2.png",
                "pet3.png",
                "pet4.png",
                "pet5.png",
                "pet6.png",
                "pet7.png",
                "pet8.png"
        );

        model.addAttribute("launchDate", launchDate);
        model.addAttribute("gameName", "Pokiguard");
        model.addAttribute("gameDescription", "Hành trình thu phục và tiến hóa các sinh vật huyền thoại!");
        model.addAttribute("bannerImage", bannerImage);
        model.addAttribute("leftPetGif", leftPetGif);
        model.addAttribute("rightPetGif", rightPetGif);
        model.addAttribute("petImages", petImages);

        return "index";
    }
}