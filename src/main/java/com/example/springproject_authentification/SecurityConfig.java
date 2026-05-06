package com.example.springproject_authentification;

// Lets us create Spring-managed objects with @Bean.
import org.springframework.context.annotation.Bean;
// Marks this class as a configuration class.
import org.springframework.context.annotation.Configuration;
// Main Spring Security object used to configure HTTP security.
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// Used to tell Spring Security not to create login sessions.
import org.springframework.security.config.http.SessionCreationPolicy;
// BCrypt is used to hash passwords safely.
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// General password hashing interface used by the app.
import org.springframework.security.crypto.password.PasswordEncoder;
// Represents the chain of security filters for incoming requests.
import org.springframework.security.web.SecurityFilterChain;
// Used to place the JWT filter before Spring's login filter.
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Tells Spring this class contains security-related bean configuration.
@Configuration
public class SecurityConfig {

    // Creates the password encoder bean so services can hash passwords.
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt hashes passwords before they are stored in the database.
        return new BCryptPasswordEncoder();
    }

    // Creates the main security rules for HTTP requests.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                // CSRF protection is turned off here because this is a REST API tested with tokens/Postman.
                .csrf(csrf -> csrf.disable())
                // Tells Spring Security not to store login state in a server session.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Starts the authorization rules for incoming requests.
                .authorizeHttpRequests(authorize -> authorize
                        // Anyone can call register and login without already being authenticated.
                        .requestMatchers("/register", "/login").permitAll()
                        // Every other endpoint requires authentication.
                        .anyRequest().authenticated()
                )
                // Disables Spring Security's default HTML login page.
                .formLogin(formLogin -> formLogin.disable())
                // Disables HTTP Basic auth because this app will use JWT instead.
                .httpBasic(httpBasic -> httpBasic.disable())
                // Runs the JWT filter before Spring's built-in username/password filter.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Builds the final security filter chain and gives it to Spring.
        return http.build();
    }
}
