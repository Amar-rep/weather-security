package com.example.guardian.dto;

import java.time.Instant;

import com.example.guardian.entity.AuditAction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditLogResponse {
	private Long id;
	private AuditAction action;
	private String detail;
	private Long memberId;
	private Instant createdAt;
}
