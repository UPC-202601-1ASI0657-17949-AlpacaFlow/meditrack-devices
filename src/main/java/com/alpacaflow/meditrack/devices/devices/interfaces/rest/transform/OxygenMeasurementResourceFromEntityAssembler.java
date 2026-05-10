package com.alpacaflow.meditrack.devices.devices.interfaces.rest.transform;

import com.alpacaflow.meditrack.devices.devices.domain.model.entities.OxygenMeasurement;
import com.alpacaflow.meditrack.devices.devices.interfaces.rest.resources.OxygenMeasurementResource;

/**
 * Assembler to convert OxygenMeasurement entity to OxygenMeasurementResource
 */
public class OxygenMeasurementResourceFromEntityAssembler {
    /**
     * Convert an OxygenMeasurement entity to an OxygenMeasurementResource
     * @param entity The entity to convert
     * @return The OxygenMeasurementResource
     */
    public static OxygenMeasurementResource toResourceFromEntity(OxygenMeasurement entity) {
        return new OxygenMeasurementResource(
                entity.getId(),
                entity.getSpo2(),
                entity.getMeasuredAt().toString()
        );
    }
}
