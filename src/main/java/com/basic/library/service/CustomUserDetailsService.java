package com.basic.library.service;

import com.basic.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<com.basic.library.domain.User> byLogin = userRepository.findByUsername(username);

        return byLogin.map(user ->
                        User.builder()
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .roles(user.getRole().name())
                            .build())
                      .orElse(null);

    }
}
