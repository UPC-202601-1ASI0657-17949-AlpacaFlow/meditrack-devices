package com.alpacaflow.meditrack.devices.devices.interfaces.rest.transform;

import com.alpacaflow.meditrack.devices.devices.domain.model.commands.AddBloodPressureMeasurementToDeviceCommand;
import com.alpacaflow.meditrack.devices.devices.interfaces.rest.resources.AddBloodPressureMeasurementToDeviceResource;

/**
 * Assembler to convert AddBloodPressureMeasurementToDeviceResource to AddBloodPressureMeasurementToDeviceCommand
 */
public class AddBloodPressureMeasurementToDeviceCommandFromResourceAssembler {
    /**
     * Convert a resource to a command
     * @param resource The resource to convert
     * @param deviceId The device ID
     * @return The AddBloodPressureMeasurementToDeviceCommand
     */
    public static AddBloodPressureMeasurementToDeviceCommand toCommandFromResource(
            AddBloodPressureMeasurementToDeviceResource resource, Long deviceId) {
        return new AddBloodPressureMeasurementToDeviceCommand(
                resource.diastolic(), resource.systolic(), deviceId);
    }
}