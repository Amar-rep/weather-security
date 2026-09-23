package com.example.guardian.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.guardian.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

}
