package com.styazhkov.InstaBizAnalyticsApplication.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.styazhkov.InstaBizAnalyticsApplication.service.InstagramApiService;
import com.styazhkov.InstaBizAnalyticsApplication.service.UserService;

@Controller
public class AuthController {

    private final UserService userService;
    private final InstagramApiService instagramApiService;

    // Явный конструктор вместо @RequiredArgsConstructor
    public AuthController(UserService userService, InstagramApiService instagramApiService) {
        this.userService = userService;
        this.instagramApiService = instagramApiService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return "redirect:/";
        }

        model.addAttribute("message", "Добро пожаловать, " + principal.getAttribute("name") + "!");

        // Здесь позже добавим автоматическую синхронизацию после логина
        return "index";  // или создай dashboard.html
    }

    @GetMapping("/sync")
    public String manualSync() {
        // Для теста — запуск синхронизации
        // Замени на реальный ID из БД
        instagramApiService.syncAccountData("17841405822304956");  // пример ID
        return "redirect:/dashboard";
    }
}