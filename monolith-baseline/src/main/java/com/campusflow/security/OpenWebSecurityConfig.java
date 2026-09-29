package com.campusflow.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Keeps the default CampusFlow quickstart and tests open when the OAuth2 Resource
 * Server dependency is on the classpath but the {@code oauth2} profile is inactive.
 */
@Configuration
@Profile("!oauth2")
public class OpenWebSecurityConfig {

    @Bean
    SecurityFilterChain openSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
        return http.build();
    }
}
