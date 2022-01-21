package ru.ifmo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .cors().disable()
            .csrf().disable()
            .requiresChannel(channel -> channel.anyRequest().requiresSecure())
            .authorizeRequests(auth -> auth.anyRequest().permitAll())
            .build();
    }

}