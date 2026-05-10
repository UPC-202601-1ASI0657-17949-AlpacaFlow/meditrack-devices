package com.alpacaflow.meditrack.devices.devices.interfaces.rest.resources;

/**
 * Resource representing a heart rate measurement
 */
public record HeartRateMeasurementResource(
        Long id,
        int bpm,
        String measuredAt
) {
}
