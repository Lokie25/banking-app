package com.banking.banking_app.Service;

import com.banking.banking_app.dto.LoginRequest;
import com.banking.banking_app.dto.RegisterRequest;
import com.banking.banking_app.dto.UserResponse;
import com.banking.banking_app.Entity.User;
import com.banking.banking_app.Repository.UserRepository;
import com.banking.banking_app.exception.BankingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse registerUser(RegisterRequest request) {

        // 1. Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BankingException("Email already registered!", HttpStatus.CONFLICT);
        }

        // 2. Build the User entity from the request
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        // 3. Save to database
        User savedUser = userRepository.save(user);

        // 4. Return a safe response (no password)
        return UserResponse.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    public UserResponse loginUser(LoginRequest request) {

        // 1. Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BankingException("User not found!", HttpStatus.NOT_FOUND));

        // 2. Check password (plain text for now)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BankingException("Invalid password!", HttpStatus.UNAUTHORIZED);
        }

        // 3. Return user info
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}