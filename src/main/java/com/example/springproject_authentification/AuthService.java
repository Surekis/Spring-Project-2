package com.example.springproject_authentification;

import com.example.springproject_authentification.DTO.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        boolean hasEmail = userRepository.existsByEmail(registerRequest.email());
        boolean hasUsername = userRepository.existsByUsername(registerRequest.username());
        if (!hasEmail && !hasUsername) {
            User user = new User(
                    registerRequest.username(),
                    passwordEncoder.encode(registerRequest.password()),
                    registerRequest.email(), Role.USER
            );
            User savedUser = userRepository.save(user);
            RegisterResponse response = new RegisterResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getRole());
            return response;
        }

        if (hasEmail) {
            throw new RuntimeException("email already exists");
        } else
            throw new RuntimeException("username already exists");


    }

    public LoginResponse login(LoginRequest loginRequest){
        User user = userRepository.findByUsername(loginRequest.username());

        if(user == null){
            throw new RuntimeException("invalid credentials");
        }
        if(passwordEncoder.matches(loginRequest.password(),user.getPassword())){
            LoginResponse loginResponse = new LoginResponse(jwtService.generateToken(user),"Bearer");
            return loginResponse;
        }

        throw new RuntimeException("invalid credentials");

    }
    public ProfileResponse changeProfile(UpdateProfileRequest updateProfileRequest){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(name);

        if (!user.getUsername().equals(updateProfileRequest.username())
                && userRepository.existsByUsername(updateProfileRequest.username())) {
            throw new RuntimeException("username already exists");
        }

        if (!user.getEmail().equals(updateProfileRequest.email())
                && userRepository.existsByEmail(updateProfileRequest.email())) {
            throw new RuntimeException("email already exists");
        }

        user.setEmail(updateProfileRequest.email());
        user.setUsername(updateProfileRequest.username());
        User savedUser = userRepository.save(user);
        return new ProfileResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
    }

    public ProfileResponse getProfile(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(name);
        return new ProfileResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}
