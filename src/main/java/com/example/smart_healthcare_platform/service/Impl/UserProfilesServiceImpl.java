package com.example.smart_healthcare_platform.service.Impl;


import com.example.smart_healthcare_platform.model.User;
import com.example.smart_healthcare_platform.model.UserProfiles;
import com.example.smart_healthcare_platform.repository.IUserProfileRepository;
import com.example.smart_healthcare_platform.service.IUserProfilesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfilesServiceImpl implements IUserProfilesService {
    private final IUserProfileRepository userProfileRepository;
    @Override
    public List<UserProfiles> getUserProfiles() {
        return userProfileRepository.findAll();
    }

    @Override
    public void saveUserProfiles(UserProfiles userProfiles) {
        userProfileRepository.save(userProfiles);
    }

    @Override
    public UserProfiles getUserProfilesByUser(User user) {
        return userProfileRepository.findByUser(user);
    }
}
