package com.duckduckgogogo.security.service;

import com.duckduckgogogo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = null;

        if (username != null) {
            username = username.trim().toLowerCase();
            if (!username.isEmpty()) {
                user = userService.findByUsername(username);
            }
            if (user == null) {
                user = userService.findByEmail(username);
            }
        }

        return user;
    }
}
