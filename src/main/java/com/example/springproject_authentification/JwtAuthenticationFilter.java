package com.example.springproject_authentification;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Used to read and validate JWT tokens.
    private final JwtService jwtService;
    // Used to load the real user from the database.
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Reads the Authorization header from the incoming request.
        String authHeader = request.getHeader("Authorization");

        // No token header, so just continue the request.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Removes "Bearer " and keeps only the raw token value.
        String token = authHeader.substring(7);
        String username;

        try {
            // Reads the username from the token payload.
            username = jwtService.extractUsername(token);
        } catch (Exception exception) {
            // Broken or fake token: ignore it and continue without authentication.
            filterChain.doFilter(request, response);
            return;
        }

        // Only authenticate if a username exists and Spring has not already authenticated this request.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Loads the matching user from the database.
            User user = userRepository.findByUsername(username);

            if (user != null && jwtService.isTokenValid(token, user)) {
                // Creates a Spring Security authentication object for this user.
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user.getUsername(),
                                null,
                                // Converts the user's role into the authority format Spring Security expects.
                                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                        );

                // Attaches request details such as remote address.
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Stores the authenticated user in Spring Security for the rest of this request.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Passes control to the next filter in the chain.
        filterChain.doFilter(request, response);
    }
}
