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
        if (username != null && !"".equals(username.trim())) {
            user = userService.findByUsername(username.trim().toLowerCase());
        }
        if (user == null) {
            user = userService.findByEmail(username.trim().toLowerCase());
        }

        return user;
    }
}
