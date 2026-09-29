package com.example.guardian.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.guardian.entity.Member;
import com.example.guardian.entity.Role;
import com.example.guardian.repository.MemberRepository;
import com.example.guardian.security.CustomUserDetailsService;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private CustomUserDetailsService userDetailsService;

	@Test
	void loadUserByUsername_success() {
		Member member = new Member();
		member.setEmail("test@gmail.com");
		member.setPasswordHash("hashed-password");
		member.setRole(Role.USER);
		when(memberRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(member));
		UserDetails result = userDetailsService.loadUserByUsername("test@gmail.com");
		assertEquals("test@gmail.com", result.getUsername());
		assertEquals("hashed-password", result.getPassword());
		assertTrue(result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
		verify(memberRepository).findByEmail("test@gmail.com");
	}

	@Test
	void loadUserByUsername_shouldThrowException_whenUserNotFound() {
		when(memberRepository.findByEmail("missing@gmail.com")).thenReturn(Optional.empty());
		assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("missing@gmail.com"));
	}
}