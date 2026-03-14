package com.securefromscratch.busybee.auth;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UsersStorage {
    private final Map<String, UserAccount> m_users = new HashMap<>();

    public Optional<UserAccount> findByUsername(String username) {
        return Optional.ofNullable(m_users.get(username));
    }

    public UserAccount createUser(String username, String password) {
        if (m_users.containsKey(username)) {
            throw new IllegalArgumentException("User already exists: " + username);
        }

        UserAccount newAccount = new UserAccount(username, password);
        m_users.put(username, newAccount); 

        return newAccount;
    }
}
