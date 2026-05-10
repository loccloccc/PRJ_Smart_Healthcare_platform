package com.example.smart_healthcare_platform.service;

import com.example.smart_healthcare_platform.model.User;

import java.util.List;

public interface IUserService {
    List<User> getUsers();
    public User saveUser(User user);
    User getUserByEmail(String email);
    User getUserById(Long id);
}
