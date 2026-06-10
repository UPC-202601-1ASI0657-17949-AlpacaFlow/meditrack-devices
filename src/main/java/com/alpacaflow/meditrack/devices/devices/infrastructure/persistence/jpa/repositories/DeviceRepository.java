package com.alpacaflow.meditrack.devices.devices.infrastructure.persistence.jpa.repositories;

import com.alpacaflow.meditrack.devices.devices.domain.model.aggregates.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Device aggregate
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    @Modifying
    @Query(value = """
            INSERT INTO devices (id, model, status, holder_id, holder_type, created_at, updated_at)
            VALUES (:id, :model, 'ACTIVE', :holderId, 'SeniorCitizen', NOW(6), NOW(6))
            """, nativeQuery = true)
    void insertWithId(@Param("id") Long id, @Param("model") String model, @Param("holderId") Long holderId);
}
