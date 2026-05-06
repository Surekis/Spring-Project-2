package com.example.springproject_authentification;

import com.example.springproject_authentification.DTO.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final UserRepository userRepository;
    private final AuthService authService;

    public AuthController(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);
    }
    @PutMapping("/profile")
    public ProfileResponse changeProfile(@RequestBody UpdateProfileRequest updateProfileRequest){
        return authService.changeProfile(updateProfileRequest);
    }
    @GetMapping("/profile")
    public ProfileResponse getProfile(){
        return authService.getProfile();
    }

}
