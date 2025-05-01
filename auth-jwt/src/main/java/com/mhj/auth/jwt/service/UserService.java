package com.mhj.auth.jwt.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mhj.auth.jwt.entity.User;
import com.mhj.auth.jwt.repsitory.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);

        return users;
    }
}
