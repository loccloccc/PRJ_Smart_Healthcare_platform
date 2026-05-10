package com.example.smart_healthcare_platform.repository;

import com.example.smart_healthcare_platform.model.User;
import com.example.smart_healthcare_platform.model.UserProfiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserProfileRepository extends JpaRepository<UserProfiles, Long> {
    UserProfiles findByUser(User user);
}
