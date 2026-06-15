package com.alpacaflow.meditrack.devices.devices.domain.model.commands;

/**
 * Registers a device with a predetermined id linked to a senior citizen holder.
 */
public record RegisterDeviceCommand(Long deviceId, Long holderId, String model) {
    public RegisterDeviceCommand {
        if (deviceId == null || deviceId <= 0) {
            throw new IllegalArgumentException("Device id must be positive");
        }
        if (holderId == null || holderId <= 0) {
            throw new IllegalArgumentException("Holder id must be positive");
        }
        if (model == null || model.isBlank()) {
            model = "IoT Band";
        }
    }
}
