package com.example.userverwaltung.config.auth;

import com.example.userverwaltung.persistence.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public record UserDetailsServiceImpl(UserRepository userRepository) implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository
                .findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unknown user/password combination"));
        return new UserDetailsImpl(user);
    }
}
