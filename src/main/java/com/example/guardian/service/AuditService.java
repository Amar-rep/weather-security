package com.example.guardian.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.example.guardian.entity.AuditAction;
import com.example.guardian.entity.AuditLog;
import com.example.guardian.entity.Member;
import com.example.guardian.repository.AuditLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {
	private final AuditLogRepository auditLogRepository;
	
	
	public void logEvent(Member member,AuditAction auditAction,String details)
	{
		AuditLog auditLog=new AuditLog();
		auditLog.setActions(auditAction);
		auditLog.setDetails(details);
		auditLog.setUser(member);
		auditLog.setCreatedAt(Instant.now());
		auditLogRepository.save(auditLog);
		log.info("Logged event action: {} user {}",auditAction.name(),member.getEmail());
	}
}
