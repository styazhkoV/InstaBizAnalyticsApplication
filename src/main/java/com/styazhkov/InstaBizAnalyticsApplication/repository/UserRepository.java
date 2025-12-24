package com.styazhkov.InstaBizAnalyticsApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.styazhkov.InstaBizAnalyticsApplication.model.InstagramAccount;
import com.styazhkov.InstaBizAnalyticsApplication.model.Media;
import com.styazhkov.InstaBizAnalyticsApplication.model.MediaInsights;
import com.styazhkov.InstaBizAnalyticsApplication.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    // IDE уже подсказывает: можно добавить findByInstagramAccount_igUserId и т.д.
}