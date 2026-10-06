package com.example.guardian.clientTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.web.context.WebApplicationContext;

import com.example.guardian.config.SecurityConfig;
import com.example.guardian.security.JwtAuthenticationFilter;

import jakarta.servlet.FilterChain;

@SpringJUnitConfig(SecurityConfigTest.TestConfig.class)
@WebAppConfiguration
class SecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    // -------------------------------------------------------
    // Bean tests
    // -------------------------------------------------------

    @Test
    void passwordEncoderShouldBeBCrypt() {

        assertNotNull(passwordEncoder);

        assertTrue(
                passwordEncoder instanceof BCryptPasswordEncoder);
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
    void springSecurityFilterChainShouldBeCreated() {

        assertNotNull(springSecurityFilterChain);
    }

    // =======================================================
    // permitAll()
    //
    // /auth/register
    // /auth/login
    // /auth/refresh
    // /auth/logout
    // /auth/token
    // =======================================================

    @Test
    void registerShouldBePermittedWithoutAuthentication()
            throws Exception {

        mockMvc.perform(post("/auth/register"))
                .andExpect(status().isNotFound());
    }

    @Test
    void loginShouldBePermittedWithoutAuthentication()
            throws Exception {

        mockMvc.perform(post("/auth/login"))
                .andExpect(status().isNotFound());
    }

    @Test
    void refreshShouldBePermittedWithoutAuthentication()
            throws Exception {

        mockMvc.perform(post("/auth/refresh"))
                .andExpect(status().isNotFound());
    }

    @Test
    void logoutShouldBePermittedWithoutAuthentication()
            throws Exception {

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isNotFound());
    }

    @Test
    void tokenShouldBePermittedWithoutAuthentication()
            throws Exception {

        mockMvc.perform(post("/auth/token"))
                .andExpect(status().isNotFound());
    }

    // =======================================================
    // Swagger permitAll()
    // =======================================================

    @Test
    void swaggerUiShouldBePermittedWithoutAuthentication()
            throws Exception {

        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isNotFound());
    }

    @Test
    void swaggerUiHtmlShouldBePermittedWithoutAuthentication()
            throws Exception {

        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().isNotFound());
    }

    @Test
    void apiDocsShouldBePermittedWithoutAuthentication()
            throws Exception {

        mockMvc.perform(get("/v3/api-docs/test"))
                .andExpect(status().isNotFound());
    }

    // =======================================================
    // /admin/** .hasRole("ADMIN")
    // =======================================================

    @Test
    void adminEndpointShouldRejectUnauthenticatedUser()
            throws Exception {

        mockMvc.perform(get("/admin/test"))

                .andExpect(status().isUnauthorized())

                .andExpect(
                        jsonPath("$.status").value(401))

                .andExpect(
                        jsonPath("$.message")
                                .value("Authentication is required"));
    }

    @Test
    void adminEndpointShouldRejectNormalUser()
            throws Exception {

        mockMvc.perform(
                get("/admin/test")
                        .with(
                                user("user@gmail.com")
                                        .roles("USER")))

                .andExpect(status().isForbidden())

                .andExpect(
                        jsonPath("$.status").value(403))

                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "You do not have permission to access this"));
    }

    @Test
    void adminEndpointShouldAllowAdmin()
            throws Exception {

        mockMvc.perform(
                get("/admin/test")
                        .with(
                                user("admin@gmail.com")
                                        .roles("ADMIN")))

                /*
                 * 404 means security allowed the request.
                 *
                 * There is simply no controller mapped to /admin/test.
                 */
                .andExpect(status().isNotFound());
    }

    // =======================================================
    // .anyRequest().authenticated()
    // =======================================================

    @Test
    void unknownEndpointShouldRequireAuthentication()
            throws Exception {

        mockMvc.perform(get("/some/protected/api"))

                .andExpect(status().isUnauthorized())

                .andExpect(
                        jsonPath("$.status").value(401));
    }

    @Test
    void authenticatedUserShouldPassAnyRequestRule()
            throws Exception {

        mockMvc.perform(
                get("/some/protected/api")
                        .with(
                                user("user@gmail.com")
                                        .roles("USER")))

                /*
                 * Security allows it.
                 * No controller exists -> 404.
                 */
                .andExpect(status().isNotFound());
    }

    // =======================================================
    // CSRF disabled
    // =======================================================

    @Test
    void postRequestShouldNotRequireCsrfToken()
            throws Exception {

        mockMvc.perform(
                post("/some/protected/api")
                        .with(
                                user("user@gmail.com")
                                        .roles("USER")))

                /*
                 * If CSRF were enabled this would normally be 403,
                 * because we did not provide a CSRF token.
                 *
                 * 404 means request passed security.
                 */
                .andExpect(status().isNotFound());
    }

    // =======================================================
    // Test configuration
    // =======================================================

    @TestConfiguration
    @EnableWebSecurity
    @Import(SecurityConfig.class)
    static class TestConfig {

        @Bean
        UserDetailsService userDetailsService() {

            return username -> User
                    .withUsername(username)
                    .password("{noop}password")
                    .roles("USER")
                    .build();
        }

        @Bean
        JwtAuthenticationFilter jwtAuthenticationFilter()
                throws Exception {

            JwtAuthenticationFilter filter = mock(JwtAuthenticationFilter.class);

            /*
             * We are testing SecurityConfig, not JWT validation.
             *
             * Therefore make JwtAuthenticationFilter simply
             * continue to the next filter.
             */
            doAnswer(invocation -> {

                FilterChain chain = invocation.getArgument(2);

                chain.doFilter(
                        invocation.getArgument(0),
                        invocation.getArgument(1));

                return null;

            }).when(filter)
                    .doFilter(any(), any(), any());

            return filter;
        }
    }
}
