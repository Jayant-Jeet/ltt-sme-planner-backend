package com.edulearnorg.ltt.smeplanner.service;

import com.edulearnorg.ltt.smeplanner.config.JwtUtil;
import com.edulearnorg.ltt.smeplanner.dto.LoginRequest;
import com.edulearnorg.ltt.smeplanner.dto.LoginResponse;
import com.edulearnorg.ltt.smeplanner.entity.User;
import com.edulearnorg.ltt.smeplanner.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }
    
    public LoginResponse authenticate(LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }
        
        User user = userOptional.get();
        
        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole().toString());
        
        return new LoginResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().toString());
    }
}
