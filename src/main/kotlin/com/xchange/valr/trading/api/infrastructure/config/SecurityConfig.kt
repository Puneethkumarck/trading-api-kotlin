package com.xchange.valr.trading.api.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(antMatcher("/swagger-ui/**"), antMatcher("/v3/api-docs/**"))
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }
            .httpBasic()
            .and()
            .build()

    @Bean
    fun userDetailsService(passwordEncoder: PasswordEncoder): UserDetailsService =
        InMemoryUserDetailsManager(
            User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .roles("USER")
                .build(),
        )

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
