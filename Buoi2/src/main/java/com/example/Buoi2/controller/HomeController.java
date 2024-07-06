package com.example.Buoi2.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String hello(Model model, Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            model.addAttribute("layout", "admin_layout");
        } else {
            model.addAttribute("layout", "layout");
        }
        model.addAttribute("message", "Chào mừng đến trang chủ");
        return "home/home";
    }
}
