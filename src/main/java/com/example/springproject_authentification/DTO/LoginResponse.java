package com.example.springproject_authentification.DTO;

public record LoginResponse(
        String token,
        String type
) {
}
