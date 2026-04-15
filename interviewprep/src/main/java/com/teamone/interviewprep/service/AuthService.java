package com.teamone.interviewprep.service;

import com.teamone.interviewprep.entity.User;

public interface AuthService {

    User register(User user);

    User login(String email, String password);
}