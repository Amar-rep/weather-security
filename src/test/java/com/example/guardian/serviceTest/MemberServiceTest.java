package com.example.guardian.serviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.guardian.entity.Member;
import com.example.guardian.exception.MemberNotFoundException;
import com.example.guardian.repository.MemberRepository;
import com.example.guardian.service.MemberService;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private MemberService memberService;

	@Test
	void findByEmail_shouldReturnMember() {
		Member member = new Member();
		when(memberRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(member));
		Member result = memberService.findByEmail("test@gmail.com");
		assertEquals(member, result);
		verify(memberRepository).findByEmail("test@gmail.com");
	}

	@Test
	void findByEmail_shouldThrowException_whenMemberNotFound() {
		when(memberRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());
		assertThrows(MemberNotFoundException.class, () -> memberService.findByEmail("test@gmail.com"));
		verify(memberRepository).findByEmail("test@gmail.com");
	}

	@Test
	void existsByEmail_shouldReturnTrue() {
		when(memberRepository.existsByEmail("test@gmail.com")).thenReturn(true);
		boolean result = memberService.existsByEmail("test@gmail.com");
		assertTrue(result);
		verify(memberRepository).existsByEmail("test@gmail.com");
	}

	@Test
	void existsByEmail_shouldReturnFalse() {
		when(memberRepository.existsByEmail("test@gmail.com")).thenReturn(false);
		boolean result = memberService.existsByEmail("test@gmail.com");
		assertFalse(result);
		verify(memberRepository).existsByEmail("test@gmail.com");
	}
}