package com.alpacaflow.meditrack.devices.devices.interfaces.rest.transform;

import com.alpacaflow.meditrack.devices.devices.domain.model.commands.AddTemperatureMeasurementToDeviceCommand;
import com.alpacaflow.meditrack.devices.devices.interfaces.rest.resources.AddTemperatureMeasurementToDeviceResource;

/**
 * Assembler to convert AddTemperatureMeasurementToDeviceResource to AddTemperatureMeasurementToDeviceCommand
 */
public class AddTemperatureMeasurementToDeviceCommandFromResourceAssembler {
    /**
     * Convert a resource to a command
     * @param resource The resource to convert
     * @param deviceId The device ID
     * @return The AddTemperatureMeasurementToDeviceCommand
     */
    public static AddTemperatureMeasurementToDeviceCommand toCommandFromResource(
            AddTemperatureMeasurementToDeviceResource resource, Long deviceId) {
        return new AddTemperatureMeasurementToDeviceCommand(resource.celsius(), deviceId);
    }
}
