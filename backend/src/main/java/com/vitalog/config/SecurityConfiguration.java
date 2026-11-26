package com.vitalog.config;

import com.vitalog.repository.UserAccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Locale;

@Configuration
public class SecurityConfiguration {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    UserDetailsService userDetailsService(UserAccountRepository userAccountRepository) {
        return email -> userAccountRepository.findById(email.trim().toLowerCase(Locale.ROOT))
                .map(account -> User.withUsername(account.email())
                        .password(account.passwordHash())
                        .roles("USER")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
    }

    @Bean
    AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider) throws Exception {
        http
                .authenticationProvider(authenticationProvider)
                .csrf(csrf -> csrf.spa())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/favicon.ico",
                                "/static/**",
                                "/api/csrf",
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/logout")
                        .permitAll()
                        .requestMatchers("/api/auth/me", "/api/habits", "/api/habits/**").authenticated()
                        .anyRequest().denyAll())
                .formLogin(form -> form
                        .loginPage("/")
                        .loginProcessingUrl("/api/auth/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) ->
                                response.setStatus(HttpStatus.NO_CONTENT.value()))
                        .failureHandler((request, response, exception) ->
                                response.sendError(HttpStatus.UNAUTHORIZED.value()))
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) ->
                                response.setStatus(HttpStatus.NO_CONTENT.value()))
                        .permitAll())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) ->
                                response.sendError(HttpStatus.UNAUTHORIZED.value())));

        return http.build();
    }
}