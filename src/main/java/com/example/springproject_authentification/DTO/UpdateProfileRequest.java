package com.example.springproject_authentification.DTO;

public record UpdateProfileRequest(
        String username,
        String email,
        String password
) {
}
