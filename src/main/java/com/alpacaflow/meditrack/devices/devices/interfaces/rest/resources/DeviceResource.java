package com.alpacaflow.meditrack.devices.devices.interfaces.rest.resources;

/**
 * Resource representing a device
 */
public record DeviceResource(
        Long id,
        String model,
        String status,
        Long holderId,
        String holderType
) {
}
