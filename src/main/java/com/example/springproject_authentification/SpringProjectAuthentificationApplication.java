package com.example.springproject_authentification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class SpringProjectAuthentificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringProjectAuthentificationApplication.class, args);
    }
}
