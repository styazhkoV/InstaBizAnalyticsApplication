package com.styazhkov.InstaBizAnalyticsApplication.controller;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.styazhkov.InstaBizAnalyticsApplication.model.InstagramAccount;
import com.styazhkov.InstaBizAnalyticsApplication.model.User;
import com.styazhkov.InstaBizAnalyticsApplication.service.InstagramApiService;
import com.styazhkov.InstaBizAnalyticsApplication.service.UserService;

import java.time.LocalDateTime;

@Controller
public class OAuthSuccessController {

    private final InstagramApiService instagramApiService;
    private final UserService userService;

    // Явный конструктор — Spring внедрит зависимости
    public OAuthSuccessController(InstagramApiService instagramApiService, UserService userService) {
        this.instagramApiService = instagramApiService;
        this.userService = userService;
    }

    @GetMapping("/oauth-success")
    public String handleOAuthSuccess(
            @RegisteredOAuth2AuthorizedClient("instagram") OAuth2AuthorizedClient client,
            Model model) {

        String shortLivedToken = client.getAccessToken().getTokenValue();

        // Обмен на long-lived token
        String longLivedToken = instagramApiService.exchangeToLongLivedToken(shortLivedToken);

        if (longLivedToken == null) {
            model.addAttribute("message", "Ошибка обмена токена. Попробуйте снова.");
            return "index";
        }

        // Получаем Instagram Business Account ID
        String igUserId = instagramApiService.getInstagramAccountId(longLivedToken);

        if (igUserId == null) {
            model.addAttribute("message", "Не удалось найти Instagram Business Account. Убедитесь, что аккаунт Business и связан с Facebook Page.");
            return "index";
        }

        // Сохраняем в БД
        User user = userService.createUser("instagram_user");  // Можно улучшить, взяв имя из principal
        InstagramAccount account = new InstagramAccount();
        account.setIgUserId(igUserId);
        account.setAccessToken(longLivedToken);
        account.setUsername("your_username");  // Можно улучшить
        account.setTokenExpiresAt(LocalDateTime.now().plusDays(60));
        account.setUser(user);
        userService.saveInstagramAccount(account);

        // Автоматическая синхронизация
        instagramApiService.syncAccountData(igUserId);

        model.addAttribute("message", "Instagram успешно подключён! Данные синхронизированы. IG ID: " + igUserId);
        return "index";  // или "dashboard"
    }
}