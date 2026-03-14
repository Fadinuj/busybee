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
        String password = (req.password == null) ? "" : req.password.trim();

        // ולידציה בסיסית (אפשר להקשיח לפי דרישה)
        if (username.length() < 3) {
            return ResponseEntity.status(401).body(Map.of("error", "Username must be at least 3 characters"));
        }
        if (password.length() < 8) {
            return ResponseEntity.status(401).body(Map.of("error", "Password must be at least 8 characters"));
        }

        // אם המשתמש כבר קיים
        if (usersStorage.findByUsername(username).isPresent()) {
            return ResponseEntity.status(401).body(Map.of("error", "Username already exists"));
        }

        // שמירה בצורה בטוחה: רק HASH
        String hashed = passwordEncoder.encode(password);
        usersStorage.createUser(username, hashed);

        // מה שה-frontend מצפה: redirectTo
        return ResponseEntity.ok(Map.of("redirectTo", "/login"));
    }
}