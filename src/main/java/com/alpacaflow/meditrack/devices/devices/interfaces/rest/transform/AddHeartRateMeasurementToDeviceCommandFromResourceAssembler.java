package com.alpacaflow.meditrack.devices.devices.interfaces.rest.transform;

import com.alpacaflow.meditrack.devices.devices.domain.model.commands.AddHeartRateMeasurementToDeviceCommand;
import com.alpacaflow.meditrack.devices.devices.interfaces.rest.resources.AddHeartRateMeasurementToDeviceResource;

/**
 * Assembler to convert AddHeartRateMeasurementToDeviceResource to AddHeartRateMeasurementToDeviceCommand
 */
public class AddHeartRateMeasurementToDeviceCommandFromResourceAssembler {
    /**
     * Convert a resource to a command
     * @param resource The resource to convert
     * @param deviceId The device ID
     * @return The AddHeartRateMeasurementToDeviceCommand
     */
    public static AddHeartRateMeasurementToDeviceCommand toCommandFromResource(
            AddHeartRateMeasurementToDeviceResource resource, Long deviceId) {
        return new AddHeartRateMeasurementToDeviceCommand(resource.bpm(), deviceId);
    }
}
