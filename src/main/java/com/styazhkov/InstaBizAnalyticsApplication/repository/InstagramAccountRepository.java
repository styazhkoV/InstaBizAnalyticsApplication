package com.styazhkov.InstaBizAnalyticsApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.styazhkov.InstaBizAnalyticsApplication.model.InstagramAccount;
import com.styazhkov.InstaBizAnalyticsApplication.model.Media;
import com.styazhkov.InstaBizAnalyticsApplication.model.MediaInsights;
import com.styazhkov.InstaBizAnalyticsApplication.model.User;

import java.util.Optional;

@Repository
public interface InstagramAccountRepository extends JpaRepository<InstagramAccount, String> {

    Optional<InstagramAccount> findByUsername(String username);

    // По ID Instagram пользователя (igUserId — это primary key)
}
