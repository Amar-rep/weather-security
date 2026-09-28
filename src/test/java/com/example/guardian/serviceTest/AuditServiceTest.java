package com.example.guardian.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.guardian.entity.AuditAction;
import com.example.guardian.entity.AuditLog;
import com.example.guardian.entity.Member;
import com.example.guardian.exception.MemberNotFoundException;
import com.example.guardian.repository.AuditLogRepository;
import com.example.guardian.repository.MemberRepository;
import com.example.guardian.service.AuditService;

@ExtendWith(MockitoExtension.class)
public class AuditServiceTest {

	@Mock
	private AuditLogRepository auditLogRepository;

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private AuditService auditService;

	@Test
	void logEvent_success() {
		Member member = new Member();
		member.setEmail("test@gmail.com");
		auditService.logEvent(member, AuditAction.CITY_ADDED, "events");
		ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
		verify(auditLogRepository).save(captor.capture());
		AuditLog savedAudit = captor.getValue();
		assertEquals(member, savedAudit.getUser());
		assertEquals(AuditAction.CITY_ADDED, savedAudit.getActions());

	}

	@Test
	void logEventEmail_success() {
		Member member = new Member();
		member.setEmail("user@gmail.com");
		when(memberRepository.findByEmail("user@gmail.com")).thenReturn(Optional.of(member));
		auditService.logEventEmail("user@gmail.com", AuditAction.CITY_ADDED, "city add");
		verify(memberRepository).findByEmail("user@gmail.com");
		verify(auditLogRepository).save(any(AuditLog.class));
	}

	@Test
	void logEvent_email_userNotFound() {
		when(memberRepository.findByEmail("user@gmail.com")).thenReturn(Optional.empty());
		assertThrows(MemberNotFoundException.class,
				() -> auditService.logEventEmail("user@gmail.com", AuditAction.CITY_ADDED, "reason"));

	}

}
