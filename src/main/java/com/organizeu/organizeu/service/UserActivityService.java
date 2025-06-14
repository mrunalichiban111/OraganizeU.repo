package com.organizeu.organizeu.service;

import com.organizeu.organizeu.model.User;
import com.organizeu.organizeu.model.UserActivity;
import com.organizeu.organizeu.repository.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserActivityService {
    @Autowired
    private UserActivityRepository userActivityRepository;

    public void logActivity(User user, String action, String details) {
        UserActivity activity = new UserActivity();
        activity.setUser(user);
        activity.setAction(action);
        activity.setDetails(details);
        userActivityRepository.save(activity);
    }

    public List<UserActivity> getRecentActivities(User user, int max) {
        List<UserActivity> all = userActivityRepository.findRecentActivities(user);
        return all.size() > max ? all.subList(0, max) : all;
    }

    public List<UserActivity> findRecentActivitiesByUser(User user, int limit) {
        return userActivityRepository.findRecentActivitiesByUser(user, limit);
    }

    public List<UserActivity> findByUserOrderByTimestampDesc(User user) {
        return userActivityRepository.findByUserOrderByTimestampDesc(user);
    }
} 