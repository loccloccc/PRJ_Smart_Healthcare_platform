package com.example.smart_healthcare_platform.service.Impl;

import com.example.smart_healthcare_platform.model.User;
import com.example.smart_healthcare_platform.repository.IUserRepository;
import com.example.smart_healthcare_platform.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements IUserService {
    private final IUserRepository repository;

    @Override
    public List<User> getUsers() {
        return repository.findAll();
    }

    @Override
    public User  saveUser(User user) {
        return repository.save(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return repository.findAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }
    @Override
    public User getUserById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
