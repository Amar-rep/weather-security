package com.example.guardian.configTest;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;

import com.example.guardian.config.SecurityConfig;
import com.example.guardian.security.JwtAuthenticationFilter;

@SpringJUnitConfig(SecurityConfigTest.TestConfig.class)
@WebAppConfiguration
class SecurityConfigTest {

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private AuthenticationProvider authenticationProvider;

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private SecurityFilterChain securityFilterChain;

        @Autowired
        private AuthenticationEntryPoint authenticationEntryPoint;

        @Autowired
        private AccessDeniedHandler accessDeniedHandler;

        @Test
        void passwordEncoderShouldBeCreated() {

                assertNotNull(passwordEncoder);
                assertInstanceOf(BCryptPasswordEncoder.class, passwordEncoder);
        }

        @Test
        void authenticationProviderShouldBeCreated() {

                assertNotNull(authenticationProvider);
                assertInstanceOf(
                                DaoAuthenticationProvider.class,
                                authenticationProvider);
        }

        @Test
        void authenticationManagerShouldBeCreated() {

                assertNotNull(authenticationManager);
        }

        @Test
        void securityFilterChainShouldBeCreated() {

                assertNotNull(securityFilterChain);
        }

        @Test
        void authenticationEntryPointShouldBeCreated() {

                assertNotNull(authenticationEntryPoint);
        }

        @Test
        void accessDeniedHandlerShouldBeCreated() {

                assertNotNull(accessDeniedHandler);
        }

        @TestConfiguration
        @EnableWebSecurity
        @Import(SecurityConfig.class)
        static class TestConfig {

                @Bean
                JwtAuthenticationFilter jwtAuthenticationFilter() {

                        return mock(JwtAuthenticationFilter.class);
                }

                @Bean
                UserDetailsService userDetailsService() {

                        return username -> User
                                        .withUsername(username)
                                        .password("{noop}password")
                                        .roles("USER")
                                        .build();
                }
        }
}