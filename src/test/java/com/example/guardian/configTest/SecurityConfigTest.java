package com.example.guardian.configTest;



import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.guardian.config.SecurityConfig;
import com.example.guardian.security.JwtAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebMvcTest(SecurityConfigTest.TestController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    /*
     * SecurityConfig requires JwtAuthenticationFilter.
     *
     * We mock it because JWT authentication itself should be tested
     * separately. Here we only want to test authorization rules.
     */
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /*
     * Required by:
     *
     * AuthenticationProvider authenticationProvider(
     *      UserDetailsService userDetailsService,
     *      PasswordEncoder passwordEncoder)
     */
    @MockitoBean
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setupJwtFilter() throws Exception {

        /*
         * Make the mocked JWT filter simply continue to the next filter.
         *
         * Otherwise a mocked filter would stop the request here.
         */
        doAnswer(invocation -> {

            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain filterChain = invocation.getArgument(2);

            filterChain.doFilter(request, response);

            return null;

        }).when(jwtAuthenticationFilter)
                .doFilter(any(), any(), any());
    }

    /*
     * Small controller used only for testing SecurityConfig.
     */
    @RestController
    static class TestController {

        @GetMapping("/auth/login")
        public String login() {
            return "login";
        }

        @GetMapping("/private")
        public String privateEndpoint() {
            return "private";
        }

        @GetMapping("/admin/test")
        public String adminEndpoint() {
            return "admin";
        }
    }

    // ---------------------------------------------------------
    // permitAll()
    // ---------------------------------------------------------

    @Test
    void loginEndpointShouldBeAccessibleWithoutAuthentication()
            throws Exception {

        mockMvc.perform(get("/auth/login"))

                .andExpect(status().isOk())

                .andExpect(content().string("login"));
    }

    // ---------------------------------------------------------
    // authenticated()
    // ---------------------------------------------------------

    @Test
    void privateEndpointWithoutAuthenticationShouldReturn401()
            throws Exception {

        mockMvc.perform(get("/private"))

                .andExpect(status().isUnauthorized())

                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.status").value(401))

                .andExpect(jsonPath("$.message")
                        .value("Authentication is required"));
    }

    // ---------------------------------------------------------
    // Normal authenticated USER
    // ---------------------------------------------------------

    @Test
    void authenticatedUserShouldAccessPrivateEndpoint()
            throws Exception {

        mockMvc.perform(
                get("/private")
                        .with(
                                user("user@gmail.com")
                                        .roles("USER")
                        )
        )

                .andExpect(status().isOk())

                .andExpect(content().string("private"));
    }

    // ---------------------------------------------------------
    // USER trying ADMIN endpoint
    // ---------------------------------------------------------

    @Test
    void userShouldNotAccessAdminEndpoint()
            throws Exception {

        mockMvc.perform(
                get("/admin/test")
                        .with(
                                user("user@gmail.com")
                                        .roles("USER")
                        )
        )

                .andExpect(status().isForbidden())

                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.status").value(403))

                .andExpect(jsonPath("$.message")
                        .value(
                                "You do not have permission to access this"
                        ));
    }

    // ---------------------------------------------------------
    // ADMIN accessing ADMIN endpoint
    // ---------------------------------------------------------

    @Test
    void adminShouldAccessAdminEndpoint()
            throws Exception {

        mockMvc.perform(
                get("/admin/test")
                        .with(
                                user("admin@gmail.com")
                                        .roles("ADMIN")
                        )
        )

                .andExpect(status().isOk())

                .andExpect(content().string("admin"));
    }
}
