package com.example.guardian.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.guardian.dto.LoginRequest;
import com.example.guardian.dto.RegisterRequest;
import com.example.guardian.dto.TokenResponse;
import com.example.guardian.entity.AuditAction;
import com.example.guardian.entity.Member;
import com.example.guardian.entity.Role;
import com.example.guardian.exception.InvalidCredentialsException;
import com.example.guardian.exception.RegistrationException;
import com.example.guardian.repository.MemberRepository;
import com.example.guardian.service.AuditService;
import com.example.guardian.service.AuthService;
import com.example.guardian.service.JwtService;
import com.example.guardian.service.RefreshTokenService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

        @Mock
        private AuthenticationManager authenticationManager;

        @Mock
        private MemberRepository memberRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private JwtService jwtService;

        @Mock
        private RefreshTokenService refreshTokenService;

        @Mock
        private AuditService auditService;

        @InjectMocks
        private AuthService authService;

        @Test
        void register_success() {

                RegisterRequest request = new RegisterRequest();
                request.setEmail("user@gmail.com");
                request.setPassword("password");
                request.setRole("USER");

                when(memberRepository.existsByEmail("user@gmail.com"))
                                .thenReturn(false);

                when(passwordEncoder.encode("password"))
                                .thenReturn("encodedPassword");

                authService.register(request);

                ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
                verify(memberRepository).save(captor.capture());

                Member savedMember = captor.getValue();
                assertEquals(request.getEmail(), savedMember.getEmail());
                verify(memberRepository).save(any(Member.class));

                verify(auditService).logEvent(
                                any(Member.class),
                                eq(AuditAction.USER_REGISTERED),
                                eq("user registered"));
        }

        @Test
        void register_EmailExists() {

                RegisterRequest request = new RegisterRequest();
                request.setEmail("user@gmail.com");

                when(memberRepository.existsByEmail("user@gmail.com"))
                                .thenReturn(true);

                assertThrows(
                                RegistrationException.class,
                                () -> authService.register(request));

                verify(memberRepository, never()).save(any());
        }

        @Test
        void login_success() {

                LoginRequest request = new LoginRequest();
                request.setEmail("user@gmail.com");
                request.setPassword("password");

                UserDetails userDetails = User
                                .withUsername("user@gmail.com")
                                .password("encodedPassword")
                                .roles("USER")
                                .build();
                
                Authentication authentication = mock(Authentication.class);

                when(authenticationManager.authenticate(any()))
                                .thenReturn(authentication);

                when(authentication.getPrincipal())
                                .thenReturn(userDetails);

                Member member = new Member();
                member.setEmail("user@gmail.com");
                member.setRole(Role.USER);

                when(memberRepository.findByEmail("user@gmail.com"))
                                .thenReturn(Optional.of(member));

                when(jwtService.generateToken(userDetails))
                                .thenReturn("access-token");

                when(refreshTokenService.createToken(
                                eq(member),
                                any(UUID.class)))
                                .thenReturn("refresh-token");

                ReflectionTestUtils.setField(
                                authService,
                                "expiration",
                                Duration.ofMinutes(15));

                TokenResponse response = authService.login(request);
                
                assertEquals("access-token", response.accessToken());
                assertEquals("refresh-token", response.refreshToken());
        }

        @Test
        void login_fail() {
                LoginRequest request = new LoginRequest();
                request.setEmail("user@gmail.com");
                request.setPassword("password");

                when(authenticationManager.authenticate(any()))
                                .thenThrow(new InvalidCredentialsException("invalid password"));

                assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        }
}