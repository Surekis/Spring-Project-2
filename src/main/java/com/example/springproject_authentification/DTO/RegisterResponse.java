package com.example.springproject_authentification.DTO;

import com.example.springproject_authentification.Role;

public record RegisterResponse(
        Integer id,
        String username,
        String email,
        Role role
) {
}
