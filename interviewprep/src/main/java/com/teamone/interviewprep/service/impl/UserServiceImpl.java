package com.teamone.interviewprep.service.impl;

import com.teamone.interviewprep.entity.User;
import com.teamone.interviewprep.exception.ResourceNotFoundException;
import com.teamone.interviewprep.repository.UserRepository;
import com.teamone.interviewprep.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(User user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(updatedUser.getName());
                    user.setEmail(updatedUser.getEmail());
                    user.setRole(updatedUser.getRole());

                    if (updatedUser.getPasswordHash() != null && !updatedUser.getPasswordHash().isBlank()) {
                        user.setPasswordHash(passwordEncoder.encode(updatedUser.getPasswordHash()));
                    }

                    return userRepository.save(user);
                })
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}