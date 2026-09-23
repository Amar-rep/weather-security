package com.example.guardian.service;

import org.springframework.stereotype.Service;

import com.example.guardian.entity.Member;
import com.example.guardian.exception.MemberNotFoundException;
import com.example.guardian.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;

	public Member findByEmail(String email) {

		return memberRepository.findByEmail(email).orElseThrow(() -> new MemberNotFoundException("Member not found"));
	}

	public boolean existsByEmail(String email) {
		return memberRepository.existsByEmail(email);
	}
}
