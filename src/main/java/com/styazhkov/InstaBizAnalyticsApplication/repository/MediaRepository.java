package com.styazhkov.InstaBizAnalyticsApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.styazhkov.InstaBizAnalyticsApplication.model.InstagramAccount;
import com.styazhkov.InstaBizAnalyticsApplication.model.Media;
import com.styazhkov.InstaBizAnalyticsApplication.model.MediaInsights;
import com.styazhkov.InstaBizAnalyticsApplication.model.User;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, String> {

    List<Media> findByAccount_IgUserId(String igUserId);

    // Пример полезного метода: все медиа конкретного аккаунта
}
