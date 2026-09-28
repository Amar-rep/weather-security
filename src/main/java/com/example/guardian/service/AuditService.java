package com.example.guardian.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.example.guardian.entity.AuditAction;
import com.example.guardian.entity.AuditLog;
import com.example.guardian.entity.Member;
import com.example.guardian.exception.MemberNotFoundException;
import com.example.guardian.repository.AuditLogRepository;
import com.example.guardian.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {
	private final AuditLogRepository auditLogRepository;
	private final MemberRepository memberRepository;

	public void logEvent(Member member, AuditAction auditAction, String details) {
		AuditLog auditLog = new AuditLog();
		auditLog.setActions(auditAction);
		auditLog.setDetails(details);
		auditLog.setUser(member);
		auditLog.setCreatedAt(Instant.now());
		auditLogRepository.save(auditLog);
	}

	public void logEventEmail(String email, AuditAction auditAction, String details) {
		Member member = memberRepository.findByEmail(email)
				.orElseThrow(() -> new MemberNotFoundException("Member not found"));
		logEvent(member, auditAction, details);
	}

}
