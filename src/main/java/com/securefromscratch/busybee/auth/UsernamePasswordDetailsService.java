package com.securefromscratch.busybee.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsernamePasswordDetailsService implements UserDetailsService {
    private final UsersStorage m_usersStorage;

    public UsernamePasswordDetailsService(UsersStorage usersStorage) {
        this.m_usersStorage = usersStorage;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("LOGIN lookup username = [" + username + "]");

        UserAccount account = m_usersStorage.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        System.out.println("LOGIN found user = [" + account.getUsername() + "]");
        System.out.println("LOGIN stored hash = [" + account.getHashedPassword() + "]");

        return org.springframework.security.core.userdetails.User
                .withUsername(account.getUsername())
                .password(account.getHashedPassword())
                .roles("USER")
                .build();
    }

    public void createUser(String username, String password) {
        m_usersStorage.createUser(username, password);
    }
}
