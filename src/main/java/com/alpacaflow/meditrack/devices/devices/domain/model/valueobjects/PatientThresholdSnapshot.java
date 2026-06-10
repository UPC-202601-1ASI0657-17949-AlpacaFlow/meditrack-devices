package com.alpacaflow.meditrack.devices.devices.domain.model.valueobjects;

/**
 * Snapshot of patient-specific vital-sign thresholds from the clinical service.
 */
public record PatientThresholdSnapshot(
        int minBpm,
        int maxBpm,
        int minSpo2,
        double minCelsius,
        double maxCelsius
) {
    public static PatientThresholdSnapshot defaults() {
        return new PatientThresholdSnapshot(60, 100, 90, 36.0, 37.5);
    }

    public HeartRateThreshold heartRateThreshold() {
        return new HeartRateThreshold(minBpm, maxBpm);
    }

    public OxygenThreshold oxygenThreshold() {
        return new OxygenThreshold(minSpo2);
    }

    public TemperatureThreshold temperatureThreshold() {
        return new TemperatureThreshold(minCelsius, maxCelsius);
    }
}
