package com.securefromscratch.busybee.auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsersPrePopulate {
    @Bean
    CommandLineRunner createUser(UsersStorage usersStorage, PasswordEncoder passwordEncoder) {
        return args -> {
            String username = "Yariv";
            if (usersStorage.findByUsername(username).isPresent()) {
                return;
            }

            String plainPassword = "Admin123!";
            String encodedPassword = passwordEncoder.encode(plainPassword);

            usersStorage.createUser(username, encodedPassword);
        };
    }
}