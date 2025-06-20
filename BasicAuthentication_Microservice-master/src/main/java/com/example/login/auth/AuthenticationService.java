package com.example.login.auth;

import com.example.login.config.JwtService;
import com.example.login.user.Role;
import com.example.login.user.User;
import com.example.login.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Create a password encoder instance
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthenticationResponse register(RegisterRequest request) {
        // Hash the password before storing it in the database
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(hashedPassword) // Store hashed password
                .dateOfBirth(request.getDateOfBirth()) // Added new field
                .role(Role.USER)
                .build();

        userRepository.save(user);

        // Generate JWT token after successful registration
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
