package com.shubham.journal_api.service;

import com.shubham.journal_api.dto.AuthResponse;
import com.shubham.journal_api.dto.LoginRequest;
import com.shubham.journal_api.dto.RegisterRequest;
import com.shubham.journal_api.model.User;
import com.shubham.journal_api.repository.UserRepository;
import com.shubham.journal_api.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request){
        //check if username already exists
        if(userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("Username already exists");
        }

        //check if email already exists
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        //create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());

        //hash the password before saving
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashedPassword);

        //save user to database
        userRepository.save(user);

        return new AuthResponse("User registerd successfully ",user.getUsername());
    }

    //Login method
    public AuthResponse login(LoginRequest request){
        //find user by username
        User user= userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        //verify password
        //psswordEncoder.matches(plainPassword, hashedPassword)
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        //Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername());

        //Return response with token
        return new AuthResponse("Login successfull", user.getUsername(),token);
    }
}
