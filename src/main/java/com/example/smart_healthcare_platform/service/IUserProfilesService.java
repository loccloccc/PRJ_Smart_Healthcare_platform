package com.example.smart_healthcare_platform.service;

import com.example.smart_healthcare_platform.model.User;
import com.example.smart_healthcare_platform.model.UserProfiles;

import java.util.List;

public interface IUserProfilesService {
    List<UserProfiles> getUserProfiles();
    void saveUserProfiles(UserProfiles userProfiles);
    UserProfiles getUserProfilesByUser(User user);
}
