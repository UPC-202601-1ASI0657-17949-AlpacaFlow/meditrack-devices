package com.alpacaflow.meditrack.devices.devices.infrastructure.persistence.jpa.repositories;

import com.alpacaflow.meditrack.devices.devices.domain.model.aggregates.ClinicalAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClinicalAuditLogRepository extends JpaRepository<ClinicalAuditLog, Long> {

    List<ClinicalAuditLog> findByDeviceIdOrderByOccurredAtDesc(Long deviceId);

    List<ClinicalAuditLog> findAllByOrderByOccurredAtDesc();
}
