package com.alpacaflow.meditrack.devices.application.internal.commandservices;

import com.alpacaflow.meditrack.devices.devices.application.internal.commandservices.DeviceCommandServiceImpl;
import com.alpacaflow.meditrack.devices.devices.domain.exceptions.DeviceNotFoundException;
import com.alpacaflow.meditrack.devices.devices.domain.model.aggregates.Device;
import com.alpacaflow.meditrack.devices.devices.domain.model.commands.*;
import com.alpacaflow.meditrack.devices.devices.infrastructure.persistence.jpa.repositories.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceCommandServiceImplTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceCommandServiceImpl deviceCommandService;

    private Device device;

    @BeforeEach
    void setUp() {
        device = new Device("Watch", 1L);
    }

    @Test
    void shouldCreateDeviceSuccessfully() {

        var command = new CreateDeviceCommand("Watch", 1L);

        var savedDevice = new Device(command);

        ReflectionTestUtils.setField(savedDevice, "id", 1L);

        when(deviceRepository.saveAndFlush(any(Device.class)))
                .thenReturn(savedDevice);

        Long result = deviceCommandService.handle(command);

        assertEquals(1L, result);

        verify(deviceRepository, times(2))
                .saveAndFlush(any(Device.class));
    }

    @Test
    void shouldAddHeartRateMeasurementSuccessfully() {
        var command = new AddHeartRateMeasurementToDeviceCommand(80, 1L);

        when(deviceRepository.findById(1L))
                .thenReturn(Optional.of(device));

        when(deviceRepository.save(any(Device.class)))
                .thenReturn(device);

        var result = deviceCommandService.handle(command);

        assertTrue(result.isPresent());
        assertEquals(1,
                result.get().getHeartRateMeasurements().size());

        verify(deviceRepository).save(device);
    }

    @Test
    void shouldThrowExceptionWhenDeviceNotFound() {
        var command = new AddHeartRateMeasurementToDeviceCommand(80, 1L);

        when(deviceRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(DeviceNotFoundException.class,
                () -> deviceCommandService.handle(command));
    }

    @Test
    void shouldRemoveOldMeasurementWhenMoreThanSeven() {
        for (int i = 0; i < 8; i++) {
            device.addHeartRate(70 + i);
        }

        var command = new AddHeartRateMeasurementToDeviceCommand(90, 1L);

        when(deviceRepository.findById(1L))
                .thenReturn(Optional.of(device));

        when(deviceRepository.save(any(Device.class)))
                .thenReturn(device);

        deviceCommandService.handle(command);

        assertEquals(8,
                device.getHeartRateMeasurements().size());
    }
}