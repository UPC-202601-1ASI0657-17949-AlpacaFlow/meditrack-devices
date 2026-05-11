package com.alpacaflow.meditrack.devices.domain.model.aggregates;


import com.alpacaflow.meditrack.devices.devices.domain.model.aggregates.Device;
import com.alpacaflow.meditrack.devices.devices.domain.model.commands.CreateDeviceCommand;
import com.alpacaflow.meditrack.devices.devices.domain.model.entities.*;
import com.alpacaflow.meditrack.devices.devices.domain.model.valueobjects.EDeviceStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeviceTest {

    @Test
    void shouldCreateDeviceCorrectly() {
        var command = new CreateDeviceCommand("Xiaomi Band", 1L);

        var device = new Device(command);

        assertEquals("Xiaomi Band", device.getModel());
        assertEquals(EDeviceStatus.ACTIVE, device.getStatus());
        assertEquals(1L, device.getHolder().holderId());
        assertNotNull(device.getMeasurements());
        assertTrue(device.getMeasurements().isEmpty());
    }

    @Test
    void shouldAddHeartRateMeasurement() {
        var device = new Device("Apple Watch", 1L);

        device.addHeartRate(85);

        assertEquals(1, device.getHeartRateMeasurements().size());
        assertEquals(85,
                device.getHeartRateMeasurements().get(0).getBpm());
    }

    @Test
    void shouldAddTemperatureMeasurement() {
        var device = new Device("Thermometer", 1L);

        device.addTemperature(36.5);

        assertEquals(1, device.getTemperatureMeasurements().size());
    }

    @Test
    void shouldAddOxygenMeasurement() {
        var device = new Device("Oximeter", 1L);

        device.addOxygen(95);

        assertEquals(1, device.getOxygenMeasurements().size());
    }

    @Test
    void shouldReturnMeasurementsByType() {
        var device = new Device("Watch", 1L);

        device.addHeartRate(80);
        device.addHeartRate(90);
        device.addTemperature(36.8);

        assertEquals(2,
                device.getMeasurementsOfType(HeartRateMeasurement.class).size());

    }
}
