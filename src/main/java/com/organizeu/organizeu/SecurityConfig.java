package com.organizeu.organizeu;
import com.organizeu.organizeu.service.CustomOAuth2UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
        logger.info("SecurityConfig initialized with CustomOAuth2UserService");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring security filter chain");
        
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/css/**", "/js/**", "/images/**", "/webjars/**", "/assets/**", "/error", "/access-denied", "/aboutus", "/calendar", "/tasks", "/notes", "/register", "/login2", "/health", "/"
                ).permitAll()
                .requestMatchers(
                    "/user/**", "/resources/**", "/schedule", "/resource_management"
                ).authenticated()
                .anyRequest().authenticated() // Change from permitAll() to authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login2")
                .defaultSuccessUrl("/user/dashboard", true) // Update success URL to a protected page
                .permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login2")
                .defaultSuccessUrl("/user/dashboard", true) // Update success URL to a protected page
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login2?logout")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            )
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/api/**")
            );
        logger.info("Security filter chain configured successfully");
        return http.build();
    }
}
