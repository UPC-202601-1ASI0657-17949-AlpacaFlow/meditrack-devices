package com.alpacaflow.meditrack.devices.devices.application.internal.commandservices;

import com.alpacaflow.meditrack.devices.devices.application.internal.services.PatientThresholdResolver;
import com.alpacaflow.meditrack.devices.devices.domain.exceptions.DeviceNotFoundException;
import com.alpacaflow.meditrack.devices.devices.domain.model.aggregates.Device;
import com.alpacaflow.meditrack.devices.devices.domain.model.commands.*;
import com.alpacaflow.meditrack.devices.devices.domain.model.entities.*;
import com.alpacaflow.meditrack.devices.devices.domain.model.events.AlertCreatedEvent;
import com.alpacaflow.meditrack.devices.devices.domain.model.valueobjects.PatientThresholdSnapshot;
import com.alpacaflow.meditrack.devices.devices.domain.services.DeviceCommandService;
import com.alpacaflow.meditrack.devices.devices.infrastructure.persistence.jpa.repositories.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@Service
public class DeviceCommandServiceImpl implements DeviceCommandService {
    private final DeviceRepository deviceRepository;
    private final PatientThresholdResolver patientThresholdResolver;

    public DeviceCommandServiceImpl(
            DeviceRepository deviceRepository,
            PatientThresholdResolver patientThresholdResolver) {
        this.deviceRepository = deviceRepository;
        this.patientThresholdResolver = patientThresholdResolver;
    }

    @Override
    @Transactional
    public Long handle(RegisterDeviceCommand command) {
        if (deviceRepository.existsById(command.deviceId())) {
            return command.deviceId();
        }
        try {
            deviceRepository.insertWithId(command.deviceId(), command.model(), command.holderId());
            var device = deviceRepository.findById(command.deviceId())
                    .orElseThrow(() -> new IllegalStateException("Device not found after insert"));
            generateDemoMeasurements(device);
            deviceRepository.saveAndFlush(device);
            return device.getId();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error registering device: %s".formatted(e.getMessage()), e);
        }
    }

    @Override
    public Long handle(CreateDeviceCommand command) {
        var device = new Device(command);

        try {
            var savedDevice = deviceRepository.saveAndFlush(device);
            if (savedDevice.getId() == null) {
                throw new IllegalStateException("Device ID is null after save");
            }

            generateDemoMeasurements(savedDevice);
            savedDevice = deviceRepository.saveAndFlush(savedDevice);

            return savedDevice.getId();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error saving device: %s".formatted(e.getMessage()), e);
        }
    }

    private void generateDemoMeasurements(Device device) {
        var thresholds = patientThresholdResolver.resolveForDevice(device);
        Random random = new Random();

        double[] temperatures = {
                36.0 + random.nextDouble() * 1.5,
                36.2 + random.nextDouble() * 1.3,
                36.5 + random.nextDouble() * 1.0,
                thresholds.minCelsius() + random.nextDouble() * 0.8,
                thresholds.maxCelsius() - random.nextDouble() * 0.5,
                thresholds.maxCelsius() + 0.5 + random.nextDouble() * 0.8,
                thresholds.minCelsius() - 0.8 - random.nextDouble() * 0.6
        };
        for (double temp : temperatures) {
            addTemperatureMeasurement(device, temp, thresholds);
        }

        int[] heartRates = {
                thresholds.minBpm() + random.nextInt(Math.max(1, thresholds.maxBpm() - thresholds.minBpm())),
                thresholds.minBpm() + random.nextInt(Math.max(1, thresholds.maxBpm() - thresholds.minBpm())),
                thresholds.minBpm() + random.nextInt(Math.max(1, thresholds.maxBpm() - thresholds.minBpm())),
                thresholds.minBpm() + random.nextInt(Math.max(1, thresholds.maxBpm() - thresholds.minBpm())),
                thresholds.minBpm() + random.nextInt(Math.max(1, thresholds.maxBpm() - thresholds.minBpm())),
                thresholds.maxBpm() + 5 + random.nextInt(20),
                Math.max(30, thresholds.minBpm() - 5 - random.nextInt(8))
        };
        for (int hr : heartRates) {
            addHeartRateMeasurement(device, hr, thresholds);
        }

        int[] oxygenLevels = {
                thresholds.minSpo2() + random.nextInt(Math.max(1, 100 - thresholds.minSpo2())),
                thresholds.minSpo2() + random.nextInt(Math.max(1, 100 - thresholds.minSpo2())),
                thresholds.minSpo2() + random.nextInt(Math.max(1, 100 - thresholds.minSpo2())),
                thresholds.minSpo2() + random.nextInt(Math.max(1, 100 - thresholds.minSpo2())),
                thresholds.minSpo2() + random.nextInt(Math.max(1, 100 - thresholds.minSpo2())),
                Math.max(50, thresholds.minSpo2() - 5 - random.nextInt(4)),
                Math.max(50, thresholds.minSpo2() - 8 - random.nextInt(5))
        };
        for (int spo2 : oxygenLevels) {
            addOxygenMeasurement(device, spo2, thresholds);
        }

        int[][] bloodPressures = {
                {70 + random.nextInt(15), 110 + random.nextInt(25)},
                {72 + random.nextInt(13), 115 + random.nextInt(20)},
                {75 + random.nextInt(10), 120 + random.nextInt(15)},
                {68 + random.nextInt(17), 112 + random.nextInt(23)},
                {74 + random.nextInt(11), 118 + random.nextInt(17)},
                {85 + random.nextInt(10), 185 + random.nextInt(15)},
                {55 + random.nextInt(10), 95 + random.nextInt(20)}
        };
        for (int[] bp : bloodPressures) {
            addBloodPressureMeasurement(device, bp[0], bp[1]);
        }
    }

    @Override
    public Optional<Device> handle(AddBloodPressureMeasurementToDeviceCommand command) {
        var device = getDeviceOrThrow(command.deviceId());
        if (device.exceedsMeasurementRetentionOfType(BloodPressureMeasurement.class)) {
            device.removeOldestMeasurementOfType(BloodPressureMeasurement.class);
        }
        addBloodPressureMeasurement(device, command.diastolic(), command.systolic());
        saveDevice(device);
        return Optional.of(device);
    }

    @Override
    public Optional<Device> handle(AddHeartRateMeasurementToDeviceCommand command) {
        var device = getDeviceOrThrow(command.deviceId());
        if (device.exceedsMeasurementRetentionOfType(HeartRateMeasurement.class)) {
            device.removeOldestMeasurementOfType(HeartRateMeasurement.class);
        }
        var thresholds = patientThresholdResolver.resolveForDevice(device);
        addHeartRateMeasurement(device, command.bpm(), thresholds);
        saveDevice(device);
        return Optional.of(device);
    }

    @Override
    public Optional<Device> handle(AddTemperatureMeasurementToDeviceCommand command) {
        var device = getDeviceOrThrow(command.deviceId());
        if (device.exceedsMeasurementRetentionOfType(TemperatureMeasurement.class)) {
            device.removeOldestMeasurementOfType(TemperatureMeasurement.class);
        }
        var thresholds = patientThresholdResolver.resolveForDevice(device);
        addTemperatureMeasurement(device, command.celsius(), thresholds);
        saveDevice(device);
        return Optional.of(device);
    }

    @Override
    public Optional<Device> handle(AddOxygenMeasurementToDeviceCommand command) {
        var device = getDeviceOrThrow(command.deviceId());
        if (device.exceedsMeasurementRetentionOfType(OxygenMeasurement.class)) {
            device.removeOldestMeasurementOfType(OxygenMeasurement.class);
        }
        var thresholds = patientThresholdResolver.resolveForDevice(device);
        addOxygenMeasurement(device, command.spo2(), thresholds);
        saveDevice(device);
        return Optional.of(device);
    }

    private Device getDeviceOrThrow(Long deviceId) {
        return deviceRepository.findById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException(deviceId));
    }

    private void saveDevice(Device device) {
        try {
            deviceRepository.save(device);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error saving device: %s".formatted(e.getMessage()));
        }
    }

    private void addHeartRateMeasurement(Device device, int bpm, PatientThresholdSnapshot thresholds) {
        var measurement = new HeartRateMeasurement(bpm, thresholds.heartRateThreshold());
        if (measurement.surpassesThreshold()) {
            device.addDomainEvent(new AlertCreatedEvent(device.getId(), bpm, measurement.getType().toString()));
        }
        device.addHeartRateMeasurement(measurement);
    }

    private void addTemperatureMeasurement(Device device, double celsius, PatientThresholdSnapshot thresholds) {
        var measurement = new TemperatureMeasurement(celsius, thresholds.temperatureThreshold());
        if (measurement.surpassesThreshold()) {
            device.addDomainEvent(new AlertCreatedEvent(device.getId(), celsius, "TEMPERATURE"));
        }
        device.addTemperatureMeasurement(measurement);
    }

    private void addOxygenMeasurement(Device device, int spo2, PatientThresholdSnapshot thresholds) {
        var measurement = new OxygenMeasurement(spo2, thresholds.oxygenThreshold());
        if (measurement.surpassesThreshold()) {
            device.addDomainEvent(new AlertCreatedEvent(device.getId(), spo2, "OXYGEN"));
        }
        device.addOxygenMeasurement(measurement);
    }

    private void addBloodPressureMeasurement(Device device, int diastolic, int systolic) {
        var measurement = new BloodPressureMeasurement(diastolic, systolic);
        if (measurement.diastolicSurpassesThreshold()) {
            device.addDomainEvent(new AlertCreatedEvent(device.getId(), diastolic, "BLOOD_PRESSURE"));
        }
        if (measurement.systolicSurpassesThreshold()) {
            device.addDomainEvent(new AlertCreatedEvent(device.getId(), systolic, "BLOOD_PRESSURE"));
        }
        device.addBloodPressure(diastolic, systolic);
    }
}
