package com.organizeu.organizeu.repository;

import com.organizeu.organizeu.model.User;
import com.organizeu.organizeu.model.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    List<UserActivity> findTop10ByUserOrderByTimestampDesc(User user);
    List<UserActivity> findByUserOrderByTimestampDesc(User user);
    @Query("SELECT a FROM UserActivity a WHERE a.user = :user ORDER BY a.timestamp DESC")
    List<UserActivity> findRecentActivities(User user);
    @Query("SELECT a FROM UserActivity a WHERE a.user = :user ORDER BY a.timestamp DESC")
    List<UserActivity> findRecentActivitiesByUser(@Param("user") User user, @Param("limit") int limit);
} 