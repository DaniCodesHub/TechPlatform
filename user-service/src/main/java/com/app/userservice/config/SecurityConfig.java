package com.app.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

import com.app.userservice.security.JwtAuthenticationFilter;
import com.app.userservice.repository.UserRepository;

@Configuration
public class SecurityConfig {

	@Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean
	SecurityFilterChain securityFilterChain(
	        HttpSecurity http,
	        JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

	    http
	        .csrf(csrf -> csrf.disable())

	        .sessionManagement(session ->
	            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        )

	        .authorizeHttpRequests(auth -> auth

	        	    // Públicos
	        	    .requestMatchers("/api/auth/login").permitAll()
	        	    .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
	        	    .requestMatchers(
	        	    	    "/swagger-ui/**",
	        	    	    "/v3/api-docs/**",
	        	    	    "/actuator/health"
	        	    	).permitAll()
	        	    
	        	    // Swagger / OpenAPI
	        	    .requestMatchers(
	        	        "/swagger-ui/**",
	        	        "/v3/api-docs/**"
	        	    ).permitAll()
    	                
    	            // Usuarios
	                .requestMatchers(HttpMethod.GET, "/api/users/**")
	                    .hasRole("ADMIN")

	                .requestMatchers(HttpMethod.DELETE, "/api/users/**")
	                    .hasRole("ADMIN")

	                .requestMatchers(HttpMethod.PATCH, "/api/users/*/role")
	                    .hasRole("ADMIN")
    	                
	        	    // Cualquier otro endpoint requiere autenticación
	        	    .anyRequest().authenticated()
	        	)

	        .addFilterBefore(
	            jwtAuthenticationFilter,
	            UsernamePasswordAuthenticationFilter.class
	        );

	    return http.build();
	}
	
	@Bean
	UserDetailsService userDetailsService(UserRepository userRepository) {

	    return username -> {
	        com.app.userservice.model.User user =
	                userRepository.findByEmail(username)
	                        .orElseThrow(() ->
	                                new UsernameNotFoundException("User not found"));

	        return org.springframework.security.core.userdetails.User
	                .withUsername(user.getEmail())
	                .password(user.getPassword())
	                .roles(user.getRole().name())
	                .build();
	    };
	}
}
