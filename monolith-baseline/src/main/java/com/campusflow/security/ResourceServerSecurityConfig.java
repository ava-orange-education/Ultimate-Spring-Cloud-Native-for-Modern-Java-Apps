package com.campusflow.security;

import java.time.Duration;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Optional OAuth2 JWT Resource Server for CampusFlow. Active only with the {@code oauth2} profile.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("oauth2")
public class ResourceServerSecurityConfig {

    @Bean
    SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/**").hasAuthority("SCOPE_campusflow.read")
                        .requestMatchers(HttpMethod.POST, "/api/**").hasAuthority("SCOPE_campusflow.write")
                        .requestMatchers(HttpMethod.PUT, "/api/**").hasAuthority("SCOPE_campusflow.write")
                        .requestMatchers(HttpMethod.PATCH, "/api/**").hasAuthority("SCOPE_campusflow.write")
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasAuthority("SCOPE_campusflow.write")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder(
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri,
            @Value("${campusflow.security.audience}") String audience) {
        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUri);

        OAuth2TokenValidator<Jwt> issuerValidator = new JwtIssuerValidator(issuerUri);
        OAuth2TokenValidator<Jwt> audienceValidator = new JwtClaimValidator<>(JwtClaimNames.AUD, aud -> {
            if (aud instanceof Collection<?> audiences) {
                return audiences.contains(audience);
            }
            return audience.equals(aud);
        });
        OAuth2TokenValidator<Jwt> timestampValidator = new JwtTimestampValidator(Duration.ofSeconds(60));

        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                issuerValidator, audienceValidator, timestampValidator));
        return jwtDecoder;
    }
}
