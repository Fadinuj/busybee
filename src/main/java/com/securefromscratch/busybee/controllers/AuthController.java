package com.securefromscratch.busybee.controllers;

import com.securefromscratch.busybee.auth.UsersStorage;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AuthController {

    private final UsersStorage usersStorage;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsersStorage usersStorage, PasswordEncoder passwordEncoder) {
        this.usersStorage = usersStorage;
        this.passwordEncoder = passwordEncoder;
    }

    // DTO קטן ל-JSON שמגיע מה-frontend
    public static class RegisterRequest {
        public String username;
        public String password;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        String username = (req.username == null) ? "" : req.username.trim();
        String password = (req.password == null) ? "" : req.password;

        System.out.println("REGISTER request username = [" + username + "]");
        System.out.println("REGISTER request password length = " + password.length());

        if (username.length() < 3) {
            System.out.println("REGISTER failed: username too short");
            return ResponseEntity.status(401).body(Map.of("error", "Username must be at least 3 characters"));
        }
        if (password.length() < 8) {
            System.out.println("REGISTER failed: password too short");
            return ResponseEntity.status(401).body(Map.of("error", "Password must be at least 8 characters"));
        }

        if (usersStorage.findByUsername(username).isPresent()) {
            System.out.println("REGISTER failed: user exists");
            return ResponseEntity.status(401).body(Map.of("error", "Username already exists"));
        }

        String hashed = passwordEncoder.encode(password);
        System.out.println("REGISTER hashed = [" + hashed + "]");
        usersStorage.createUser(username, hashed);
        System.out.println("REGISTER success for [" + username + "]");

        return ResponseEntity.ok(Map.of("redirectTo", "/login"));
    }
}