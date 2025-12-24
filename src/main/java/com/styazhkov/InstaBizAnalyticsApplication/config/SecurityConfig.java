package com.styazhkov.InstaBizAnalyticsApplication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Разрешаем доступ без авторизации
                        .requestMatchers("/", "/index.html", "/static/**", "/webjars/**",
                                "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**",
                                "/h2-console/**", "/error").permitAll()
                        // Всё остальное — только авторизованным
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        // НЕ ставим loginPage("/") — чтобы не было цикла
                        // Если не авторизован — Spring сам покажет дефолтную страницу логина
                        .defaultSuccessUrl("/dashboard", true)  // После успешного логина — на дашборд
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                )
                .headers(headers -> headers
                        // frameOptions().disable() deprecated — заменяем на deny
                        .frameOptions(frame -> frame.deny())
                );

        return http.build();
    }
}