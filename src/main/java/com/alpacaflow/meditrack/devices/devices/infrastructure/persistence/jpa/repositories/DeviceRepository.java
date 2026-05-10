package com.alpacaflow.meditrack.devices.devices.infrastructure.persistence.jpa.repositories;

import com.alpacaflow.meditrack.devices.devices.domain.model.aggregates.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Device aggregate
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
}
