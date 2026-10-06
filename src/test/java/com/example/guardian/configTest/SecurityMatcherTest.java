package com.example.guardian.configTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.guardian.config.SecurityConfig;
import com.example.guardian.security.JwtAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebMvcTest(SecurityMatcherTest.TestController.class)
@Import({ SecurityConfig.class, SecurityMatcherTest.TestController.class })
class SecurityMatcherTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private CacheManager cacheManager;

    @BeforeEach
    void setUpJwtFilter() throws Exception {

      
        doAnswer(invocation -> {

            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);

            chain.doFilter(request, response);

            return null;

        }).when(jwtAuthenticationFilter)
                .doFilter(any(), any(), any());
    }


    @Test
    void loginShouldBeAccessibleWithoutAuthentication()
            throws Exception {

        mockMvc.perform(post("/auth/login"))
                .andExpect(status().isOk());
    }

    @Test
    void registerShouldBeAccessibleWithoutAuthentication()
            throws Exception {

        mockMvc.perform(post("/auth/register"))
                .andExpect(status().isOk());
    }

  
    @Test
    void adminEndpointWithoutAuthenticationShouldReturn401()
            throws Exception {

        mockMvc.perform(get("/admin/test"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userShouldNotAccessAdminEndpoint()
            throws Exception {

        mockMvc.perform(
                get("/admin/test")
                        .with(
                                user("user@gmail.com")
                                        .roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminShouldAccessAdminEndpoint()
            throws Exception {

        mockMvc.perform(
                get("/admin/test")
                        .with(
                                user("admin@gmail.com")
                                        .roles("ADMIN")))
                .andExpect(status().isOk());
    }

   
    @RestController
    static class TestController {

        @PostMapping("/auth/login")
        String login() {
            return "login";
        }

        @PostMapping("/auth/register")
        String register() {
            return "register";
        }

        @GetMapping("/admin/test")
        String admin() {
            return "admin";
        }
    }
}
