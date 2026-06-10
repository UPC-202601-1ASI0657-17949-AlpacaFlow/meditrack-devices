package com.alpacaflow.meditrack.devices.devices.interfaces.rest.resources;

/**
 * Resource for registering a device with a predetermined id linked to a senior citizen.
 */
public record RegisterDeviceResource(Long deviceId, Long holderId, String model) {
}
