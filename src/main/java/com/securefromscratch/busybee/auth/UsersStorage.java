package com.securefromscratch.busybee.auth;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UsersStorage {
    private final Map<String, UserAccount> m_users = new HashMap<>();

    public Optional<UserAccount> findByUsername(String username) {
        System.out.println("findByUsername called with: [" + username + "]");
        System.out.println("Current users: " + m_users.keySet());
        return Optional.ofNullable(m_users.get(username));
    }

    public UserAccount createUser(String username, String password) {
        return createUser(username, password, "CREATOR");
    }

    public UserAccount createUser(String username, String password, String role) {
        if (m_users.containsKey(username)) {
            throw new IllegalArgumentException("User already exists: " + username);
        }

        UserAccount newAccount = new UserAccount(username, password, role);
        m_users.put(username, newAccount);

        System.out.println("User created: [" + username + "]");
        System.out.println("Role: [" + role + "]");
        System.out.println("All users now: " + m_users.keySet());

        return newAccount;
    }
}