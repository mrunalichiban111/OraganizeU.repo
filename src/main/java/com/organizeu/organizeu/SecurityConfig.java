package com.organizeu.organizeu;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login2", "/css/**", "/js/**", "/assets/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login2")               // maps to controller + Thymeleaf template
                .loginProcessingUrl("/do-login")    // must match form action
                .defaultSuccessUrl("/", true)       // landing page after login
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login2?logout")
                .permitAll()
            )
            // CSRF protection is enabled by default; no need to call csrf.enable()
            .httpBasic(httpBasic -> {});            // Enable HTTP Basic authentication

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("sambhav")
                .password("{noop}password") // No password encoding (for demo only)
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}
