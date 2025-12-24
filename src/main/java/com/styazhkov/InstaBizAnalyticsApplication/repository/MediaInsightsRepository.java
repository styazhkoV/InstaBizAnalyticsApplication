package com.styazhkov.InstaBizAnalyticsApplication.repository;

import org.springframework.stereotype.Repository;

import com.styazhkov.InstaBizAnalyticsApplication.model.InstagramAccount;
import com.styazhkov.InstaBizAnalyticsApplication.model.Media;
import com.styazhkov.InstaBizAnalyticsApplication.model.MediaInsights;
import com.styazhkov.InstaBizAnalyticsApplication.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaInsightsRepository extends JpaRepository<MediaInsights, Long> {
Optional<MediaInsights> findByMedia(Media media);
}
