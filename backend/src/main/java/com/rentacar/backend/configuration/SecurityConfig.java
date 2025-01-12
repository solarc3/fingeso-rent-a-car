package com.rentacar.backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configure(http))
            .authorizeHttpRequests(auth -> auth
                                       .requestMatchers("/api/usuario/login", "/api/usuario/crear").permitAll()
                                       .requestMatchers("/api/**").permitAll()
                    //cuando se modifique la api para tener los distintos roles va a funcionar esto, mientras no hace nada
                                       .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")
                                       .requestMatchers("/api/trabajador/**").hasRole("TRABAJADOR")
                                       .anyRequest().authenticated()
                                  )
            .sessionManagement(session -> session
                                   .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                              );

        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}